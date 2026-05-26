#!/usr/bin/env python3
"""Build deterministic ZDB data fixtures from the provided grassland JSON samples."""

from __future__ import annotations

import argparse
import json
import sqlite3
from pathlib import Path
from typing import Any


BAD_PLOT_ID = "FIX_BAD_001"
GOOD_PLOT_ID = "FIX_GOOD_001"
EXPECTED_ADDITIONAL_RULES = [
    "ADD_GRASS_002",
    "ADD_GRASS_003",
    "ADD_GRASS_004",
    "ADD_GRASS_011",
    "ADD_GRASS_012",
    "ADD_GRASS_018",
    "ADD_GRASS_019",
    "ADD_GRASS_020",
    "ADD_GRASS_021",
    "ADD_GRASS_029",
]


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("template_zdb", type=Path)
    parser.add_argument("main_json", type=Path)
    parser.add_argument("line_json", type=Path)
    parser.add_argument("embedded_rules_json", type=Path)
    parser.add_argument("output_zdb", type=Path)
    parser.add_argument("expected_output_json", type=Path)
    return parser.parse_args()


def read_rows(path: Path) -> list[dict[str, Any]]:
    return json.loads(path.read_text(encoding="utf-8-sig"))


def copy_read_only_template(source: Path, output: Path) -> sqlite3.Connection:
    output.parent.mkdir(parents=True, exist_ok=True)
    output.unlink(missing_ok=True)
    source_connection = sqlite3.connect(source.resolve().as_uri() + "?mode=ro", uri=True)
    source_connection.execute("PRAGMA query_only=ON")
    output_connection = sqlite3.connect(output)
    try:
        source_connection.backup(output_connection)
    finally:
        source_connection.close()
    return output_connection


def table_columns(connection: sqlite3.Connection, table: str) -> dict[str, str]:
    return {
        column[1].upper(): column[1]
        for column in connection.execute(f'PRAGMA table_info("{table}")')
    }


def insert_intersecting_row(
    connection: sqlite3.Connection,
    table: str,
    base_row: dict[str, Any],
    overrides: dict[str, Any],
) -> None:
    columns = table_columns(connection, table)
    values_by_upper_name = {str(key).upper(): value for key, value in base_row.items()}
    values_by_upper_name.update({key.upper(): value for key, value in overrides.items()})
    names = [column for key, column in columns.items() if key in values_by_upper_name]
    values = [values_by_upper_name[column.upper()] for column in names]
    placeholders = ", ".join("?" for _ in names)
    column_sql = ", ".join(f'"{column}"' for column in names)
    connection.execute(
        f'INSERT INTO "{table}" ({column_sql}) VALUES ({placeholders})',
        values,
    )


def execute_added_rules(
    connection: sqlite3.Connection, embedded_rules_path: Path, plot_id: str
) -> list[str]:
    rules = json.loads(embedded_rules_path.read_text(encoding="utf-8"))["rules"]
    matched: list[str] = []
    for rule in rules:
        if rule["sourceId"] != "grassland-additional-20260526":
            continue
        rows = connection.execute(rule["sql"], {"ydId": plot_id}).fetchall()
        if rows:
            matched.append(rule["id"])
    return matched


def main() -> None:
    args = parse_arguments()
    main_rows = read_rows(args.main_json)
    line_rows = read_rows(args.line_json)
    selected_source_id = str(main_rows[0]["YD_ID"])
    base_main = main_rows[0]
    base_line = next(row for row in line_rows if str(row["YD_ID"]) == selected_source_id)

    connection = copy_read_only_template(args.template_zdb, args.output_zdb)
    try:
        connection.execute('DELETE FROM "YD_TRCY_PT"')
        connection.execute('DELETE FROM "YX_TRCY_TB"')

        insert_intersecting_row(
            connection,
            "YD_TRCY_PT",
            base_main,
            {
                "PK_UID": 900001,
                "YD_ID": BAD_PLOT_ID,
                "MZGUID": "fixture-bad-guid",
                "MIAN_JI": 0.25,
                "ZUOBIAO_X": 123,
                "ZUOBIAO_Y": 123,
                "CDGD": 59,
                "ZBGD": 101,
                "LB_BL": 41,
                "LSFG_BL": 21,
                "YJB_BL": 21,
                "FS_HD": 11,
            },
        )
        insert_intersecting_row(
            connection,
            "YD_TRCY_PT",
            base_main,
            {
                "PK_UID": 900002,
                "YD_ID": GOOD_PLOT_ID,
                "MZGUID": "fixture-good-guid",
                "MIAN_JI": 0.125,
                "ZUOBIAO_X": 20226201,
                "ZUOBIAO_Y": 3384627,
                "CDGD": 60,
                "ZBGD": 50,
                "LB_BL": 40,
                "LSFG_BL": 20,
                "YJB_BL": 20,
                "FS_HD": 10,
            },
        )
        insert_intersecting_row(
            connection,
            "YX_TRCY_TB",
            base_line,
            {
                "PK_UID": 900011,
                "YD_ID": BAD_PLOT_ID,
                "MZGUID": "fixture-bad-line-guid",
                "XB_GLH": "fixture-bad-guid",
                "YX_ID": 1,
                "YX_CD": 40,
            },
        )
        insert_intersecting_row(
            connection,
            "YX_TRCY_TB",
            base_line,
            {
                "PK_UID": 900012,
                "YD_ID": GOOD_PLOT_ID,
                "MZGUID": "fixture-good-line-guid",
                "XB_GLH": "fixture-good-guid",
                "YX_ID": 1,
                "YX_CD": 20,
            },
        )
        connection.commit()

        bad_matches = execute_added_rules(connection, args.embedded_rules_json, BAD_PLOT_ID)
        good_matches = execute_added_rules(connection, args.embedded_rules_json, GOOD_PLOT_ID)
        if bad_matches != EXPECTED_ADDITIONAL_RULES:
            raise RuntimeError(f"Unexpected bad fixture matches: {bad_matches}")
        if good_matches:
            raise RuntimeError(f"Expected good fixture to pass; matched: {good_matches}")
    finally:
        connection.close()

    args.expected_output_json.parent.mkdir(parents=True, exist_ok=True)
    args.expected_output_json.write_text(
        json.dumps(
            {
                "sourceTemplate": args.template_zdb.name,
                "derivedFromPlotId": selected_source_id,
                "badPlotId": BAD_PLOT_ID,
                "goodPlotId": GOOD_PLOT_ID,
                "badPlotExpectedAdditionalRuleIds": EXPECTED_ADDITIONAL_RULES,
                "goodPlotExpectedAdditionalRuleIds": [],
            },
            ensure_ascii=False,
            indent=2,
        )
        + "\n",
        encoding="utf-8",
    )
    print(f"Created deterministic fixture database at {args.output_zdb}.")


if __name__ == "__main__":
    main()
