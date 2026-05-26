#!/usr/bin/env python3
"""Append the first SQL-converted grassland rules to the embedded rule set."""

from __future__ import annotations

import argparse
import json
from pathlib import Path
from typing import Any


SOURCE_ID = "grassland-additional-20260526"


def rule(
    source_number: int,
    severity: str,
    table: str,
    title: str,
    explanation: str,
    fields: list[str],
    locator_fields: list[str],
    sql: str,
) -> dict[str, Any]:
    return {
        "id": f"ADD_GRASS_{source_number:03d}",
        "sourceId": SOURCE_ID,
        "severity": severity,
        "targetTable": table,
        "title": title,
        "explanation": explanation,
        "requiredTables": [table],
        "requiredFields": fields,
        "locatorFields": locator_fields,
        "sql": sql,
    }


ADDITIONAL_RULES = [
    rule(
        2,
        "MANDATORY",
        "YD_TRCY_PT",
        "天然草原样地面积不是 0.125",
        "天然草原样地面积统一填写为 0.125。",
        ["YD_ID", "MIAN_JI"],
        ["YD_ID"],
        'SELECT YD_ID, MIAN_JI AS actualValue FROM "YD_TRCY_PT" '
        "WHERE YD_ID = :ydId AND (MIAN_JI IS NULL OR CAST(MIAN_JI AS REAL) <> 0.125)",
    ),
    rule(
        3,
        "MANDATORY",
        "YD_TRCY_PT",
        "中心桩 GNSS 坐标 X 位数异常",
        "中心桩 GNSS 坐标 X（米）的整数部分必须为 8 位。",
        ["YD_ID", "ZUOBIAO_X"],
        ["YD_ID"],
        'SELECT YD_ID, ZUOBIAO_X AS actualValue FROM "YD_TRCY_PT" '
        "WHERE YD_ID = :ydId AND "
        "(ZUOBIAO_X IS NULL OR LENGTH(CAST(ROUND(ABS(ZUOBIAO_X), 0) AS INTEGER)) <> 8)",
    ),
    rule(
        4,
        "MANDATORY",
        "YD_TRCY_PT",
        "中心桩 GNSS 坐标 Y 位数异常",
        "中心桩 GNSS 坐标 Y（米）的整数部分必须为 7 位。",
        ["YD_ID", "ZUOBIAO_Y"],
        ["YD_ID"],
        'SELECT YD_ID, ZUOBIAO_Y AS actualValue FROM "YD_TRCY_PT" '
        "WHERE YD_ID = :ydId AND "
        "(ZUOBIAO_Y IS NULL OR LENGTH(CAST(ROUND(ABS(ZUOBIAO_Y), 0) AS INTEGER)) <> 7)",
    ),
    rule(
        11,
        "ADVISORY",
        "YD_TRCY_PT",
        "植被盖度低于 60%",
        "植被盖度小于 60% 时提示人工检查核对。",
        ["YD_ID", "CDGD"],
        ["YD_ID"],
        'SELECT YD_ID, CDGD AS actualValue FROM "YD_TRCY_PT" '
        "WHERE YD_ID = :ydId AND CDGD IS NOT NULL AND CAST(CDGD AS REAL) < 60",
    ),
    rule(
        12,
        "ADVISORY",
        "YD_TRCY_PT",
        "草群平均高度超出 5 至 100",
        "草群平均高度低于 5 或高于 100 时提示人工检查核对。",
        ["YD_ID", "ZBGD"],
        ["YD_ID"],
        'SELECT YD_ID, ZBGD AS actualValue FROM "YD_TRCY_PT" '
        "WHERE YD_ID = :ydId AND ZBGD IS NOT NULL "
        "AND (CAST(ZBGD AS REAL) < 5 OR CAST(ZBGD AS REAL) > 100)",
    ),
    rule(
        18,
        "ADVISORY",
        "YD_TRCY_PT",
        "裸斑面积比例超过 40%",
        "裸斑面积比例超过 40% 时提示人工检查核对。",
        ["YD_ID", "LB_BL"],
        ["YD_ID"],
        'SELECT YD_ID, LB_BL AS actualValue FROM "YD_TRCY_PT" '
        "WHERE YD_ID = :ydId AND LB_BL IS NOT NULL AND CAST(LB_BL AS REAL) > 40",
    ),
    rule(
        19,
        "ADVISORY",
        "YD_TRCY_PT",
        "砾石覆盖面积比例超过 20%",
        "砾石覆盖面积比例超过 20% 时提示人工检查核对。",
        ["YD_ID", "LSFG_BL"],
        ["YD_ID"],
        'SELECT YD_ID, LSFG_BL AS actualValue FROM "YD_TRCY_PT" '
        "WHERE YD_ID = :ydId AND LSFG_BL IS NOT NULL AND CAST(LSFG_BL AS REAL) > 20",
    ),
    rule(
        20,
        "ADVISORY",
        "YD_TRCY_PT",
        "盐碱斑块面积比例超过 20%",
        "盐碱斑块面积比例超过 20% 时提示人工检查核对。",
        ["YD_ID", "YJB_BL"],
        ["YD_ID"],
        'SELECT YD_ID, YJB_BL AS actualValue FROM "YD_TRCY_PT" '
        "WHERE YD_ID = :ydId AND YJB_BL IS NOT NULL AND CAST(YJB_BL AS REAL) > 20",
    ),
    rule(
        21,
        "ADVISORY",
        "YD_TRCY_PT",
        "覆沙厚度超过 10 厘米",
        "覆沙厚度超过 10 厘米时提示人工检查核对。",
        ["YD_ID", "FS_HD"],
        ["YD_ID"],
        'SELECT YD_ID, FS_HD AS actualValue FROM "YD_TRCY_PT" '
        "WHERE YD_ID = :ydId AND FS_HD IS NOT NULL AND CAST(FS_HD AS REAL) > 10",
    ),
    rule(
        29,
        "MANDATORY",
        "YX_TRCY_TB",
        "样线长度不是 20 米",
        "样线长度统一填写为 20 米。",
        ["YD_ID", "YX_ID", "YX_CD"],
        ["YD_ID", "YX_ID"],
        'SELECT YD_ID, YX_ID, YX_CD AS actualValue FROM "YX_TRCY_TB" '
        "WHERE YD_ID = :ydId AND (YX_CD IS NULL OR CAST(YX_CD AS REAL) <> 20)",
    ),
]


def parse_arguments() -> argparse.Namespace:
    parser = argparse.ArgumentParser()
    parser.add_argument("rule_set", type=Path, help="Existing baseline rule-set JSON")
    return parser.parse_args()


def main() -> None:
    args = parse_arguments()
    content = json.loads(args.rule_set.read_text(encoding="utf-8"))
    content["ruleSetVersion"] = "2026.05-initial"
    content["sources"] = [
        source for source in content["sources"] if source["id"] != SOURCE_ID
    ]
    content["sources"].append(
        {
            "id": SOURCE_ID,
            "kind": "ADDITIONAL",
            "label": "草地质检附加规则首批",
            "description": "从草地样地调查质检规则表选择并 SQL 化的首批 10 条验证规则。",
        }
    )
    content["rules"] = [
        embedded_rule
        for embedded_rule in content["rules"]
        if embedded_rule["sourceId"] != SOURCE_ID
    ]
    content["rules"].extend(ADDITIONAL_RULES)
    args.rule_set.write_text(
        json.dumps(content, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    print(f"Embedded {len(ADDITIONAL_RULES)} additional rules into {args.rule_set}.")


if __name__ == "__main__":
    main()
