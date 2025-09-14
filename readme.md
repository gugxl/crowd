好的，这是一个整合了您所有需求和技术栈的完整大数据圈人营销平台方案。

大数据圈人营销平台完整方案
一、 整体架构设计

本平台采用 Lambda 架构思想，融合离线链路（批量计算与分析）和实时链路（实时处理与匹配），旨在构建一个集数据资产管理、精准圈人、活动执行、实时匹配于一体的企业级营销决策平台。

1.1 技术栈分工

组件	作用
HDFS	底层分布式存储，承载原始数据、中间计算结果、Hive 表数据文件。
Hive	离线数据仓库核心，构建分层数据模型（ODS/DWD/DWS/ADS），提供 SQL 接口进行数据查询与管理，元数据存储于 PostgreSQL。
MapReduce	基础批处理计算引擎，用于传统 ETL 或作为 Spark 的备用方案。
Spark	离线计算核心引擎，承担复杂 ETL、标签计算、人群规则引擎执行、离线人群包计算与多渠道导出。
Flink	实时计算核心引擎，处理实时数据流（用户行为），进行实时标签计算、实时人群匹配与状态更新。
Kafka	实时数据总线，用于高吞吐量地收集用户行为日志、业务变更事件、实时标签更新通知等。
HBase	实时/近实时数据存储，以 UID 为 RowKey 存储用户实时标签和所属人群编码，支持低延迟查询。
PostgreSQL	关系型数据库，存储业务元数据（标签定义、人群规则、活动配置、调度信息、数据质量结果）。
调度系统	Airflow (推荐) 或 Oozie，负责离线任务的定时调度、依赖管理与监控。
API 服务	Spring Boot 或类似框架，提供 RESTful 接口供外部系统调用，负责人群匹配、数据查询、状态管理等。

1.2 平台架构图

code
Mermaid
download
content_copy
expand_less

graph TD
subgraph 数据源层
A[业务数据库/日志文件] --> B(Flume/Sqoop/Kafka Producer)
end

    subgraph 数据湖与数仓层
        B --> C[Kafka<br/>(原始日志/事件流)]
        C --> D[Flink<br/>(实时ETL/增量入湖)]
        D --> E[HDFS<br/>(原始数据/Parquet)]
        E --> F[Hive<br/>(ODS/DWD/DWS/ADS)]
    end

    subgraph 实时计算与服务层
        C --> G[Flink<br/>(实时标签计算/人群更新)]
        G --> H[HBase<br/>(实时标签/人群归属)]
        H --> I[人群匹配平台<br/>(API服务)]
        I --> J[外部业务系统<br/>(App/CRM/广告)]
    end

    subgraph 离线计算与应用层
        F --> K[Spark<br/>(离线标签/人群计算)]
        K --> L[数据资产管理平台<br/>(UI/元数据/质检)]
        K --> M[圈人平台<br/>(UI/规则/导出)]
        M --> H
        M --> N[文件/HDFS/DB<br/>(离线人群包)]
        M --> O[活动平台<br/>(UI/活动配置)]
        O --> N
        O --> I
        O --> J
    end

    subgraph 元数据与调度层
        P[PostgreSQL<br/>(元数据/规则/配置)]
        Q[Airflow<br/>(调度系统)]
        P -- 管理 --> L
        P -- 存储规则 --> M
        P -- 存储配置 --> O
        Q -- 调度 --> K
        Q -- 调度 --> L
        Q -- 调度 --> M
    end

    style A fill:#f9f,stroke:#333,stroke-width:2px
    style B fill:#f9f,stroke:#333,stroke-width:2px
    style C fill:#ccf,stroke:#333,stroke-width:2px
    style D fill:#bbf,stroke:#333,stroke-width:2px
    style E fill:#bfb,stroke:#333,stroke-width:2px
    style F fill:#bfb,stroke:#333,stroke-width:2px
    style G fill:#bbf,stroke:#333,stroke-width:2px
    style H fill:#fbc,stroke:#333,stroke-width:2px
    style I fill:#f9f,stroke:#333,stroke-width:2px
    style J fill:#f9f,stroke:#333,stroke-width:2px
    style K fill:#bbf,stroke:#333,stroke-width:2px
    style L fill:#f9f,stroke:#333,stroke-width:2px
    style M fill:#f9f,stroke:#333,stroke-width:2px
    style N fill:#fbc,stroke:#333,stroke-width:2px
    style O fill:#f9f,stroke:#333,stroke-width:2px
    style P fill:#ccf,stroke:#333,stroke-width:2px
    style Q fill:#f9f,stroke:#333,stroke-width:2px

1.3 数据流向

离线链路：
原始数据（HDFS）→ Hive（数据仓库，ODS/DWD/DWS）→ Spark（标签/人群计算）→ 导出到 Hive/HBase/文件

实时链路：
Kafka（实时数据，如用户行为日志）→ Flink（实时标签/人群计算）→ HBase（实时标签/人群存储）→ 匹配查询

二、 模块详细设计

2.1 数据资产管理平台

目标： 统一管理平台所有数据资产，提供离线标签数据和元数据服务，确保数据质量。

核心功能：

元数据管理 (PostgreSQL/Hive Metastore)：

存储 Hive 表结构、字段定义、业务主键（UID）。

维护标签定义：tag_id, tag_code, tag_name, tag_type (枚举/数值/布尔/时间), calculation_type (离线/实时), source_table, field_name, update_freq, description。

维护标签与业务主键的关联关系。

数据仓库构建 (Hive/HDFS)：

ODS 层： 存储原始数据，如用户行为日志、业务系统快照，按 dt 分区。

DWD 层： 对 ODS 数据进行清洗、规范化、去重、维度退化，生成明细事实表。

DWS 层： 构建用户标签宽表（如 dws_user_profile_wide），聚合用户行为、属性，产出各种离线标签，按 dt 分区。

ADS 层： 存储特定应用场景（如人群包）的结果数据。

数据质检 (Spark/HiveQL)：

调度： Airflow 定时调度 Spark 任务。

检查内容： 标签数据完整性（空值率）、数据分布合理性（值域范围、倾斜度）、主键唯一性、数据量波动等。

结果： 质检结果写入 PostgreSQL 的 data_quality_report 表，并触发告警通知。

数据接口：

提供 REST API 接口供外部系统查询标签元数据、标签值分布、质检报告。

提供标准化写入接口（通过 Spark/HiveQL 或 Flink/Kafka）将数据写入 Hive/HDFS。

技术实现示例：

PostgreSQL 表结构 (tag_metadata)：

code
SQL
download
content_copy
expand_less
IGNORE_WHEN_COPYING_START
IGNORE_WHEN_COPYING_END
CREATE TABLE tag_metadata (
tag_code VARCHAR(50) PRIMARY KEY,
tag_name VARCHAR(100) NOT NULL,
tag_type VARCHAR(20) NOT NULL, -- 'enum', 'numeric', 'boolean', 'datetime'
biz_key_field VARCHAR(50) NOT NULL, -- e.g., 'uid'
source_table VARCHAR(200), -- e.g., 'dws.user_profile_wide'
source_field VARCHAR(100), -- field name in source_table
calculation_type VARCHAR(20) NOT NULL, -- 'offline', 'realtime'
update_freq VARCHAR(20), -- 'daily', 'hourly', 'realtime'
description TEXT,
create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

Hive 标签宽表 (dws.user_profile_wide)：

code
SQL
download
content_copy
expand_less
IGNORE_WHEN_COPYING_START
IGNORE_WHEN_COPYING_END
CREATE TABLE dws.user_profile_wide (
uid STRING COMMENT '用户唯一标识',
gender STRING COMMENT '性别',
age INT COMMENT '年龄',
city STRING COMMENT '城市',
last_login_dt STRING COMMENT '最后登录日期',
total_spent_30d DECIMAL(10,2) COMMENT '近30天消费金额',
is_premium_user BOOLEAN COMMENT '是否高价值用户'
)
PARTITIONED BY (dt STRING COMMENT '数据日期分区')
STORED AS PARQUET;

2.2 圈人平台

目标： 基于标签规则组合人群，支持定时调度计算、人群版本管理和多渠道导出。

核心功能：

规则引擎 (UI + PostgreSQL + Spark)：

UI 界面： 提供可视化界面，允许用户选择标签（从数据资产平台获取）、设置条件（=, >, <, BETWEEN, IN 等），并使用 AND/OR/NOT 进行逻辑组合。

规则存储： 将用户定义的规则转换为 JSON 格式，存储到 PostgreSQL 的 crowd_rules 表。

规则解析与执行： Spark 任务在执行时，读取 rule_json，将其解析为 Spark SQL 的 WHERE 子句，对 Hive 标签宽表进行过滤。

人群计算 (Spark)：

调度： Airflow 定时调度 Spark 任务，根据 crowd_rules 重新计算人群。

输入： Hive DWS 层的标签宽表。

输出： 符合规则的 UID 列表。

人群导出：

Hive 表： 生成 ads.crowd_uid_{crowd_code} 表，存储人群 UID。

文件导出： 导出 CSV/Parquet 文件到 HDFS 指定路径，供外部系统拉取。

HBase 存储： 将人群 UID 写入 HBase user_crowd_mapping 表，以 UID 为 RowKey，人群 Code 为列，值为时间戳或 1，实现实时匹配。

技术实现示例：

PostgreSQL 表结构 (crowd_rules)：

code
SQL
download
content_copy
expand_less
IGNORE_WHEN_COPYING_START
IGNORE_WHEN_COPYING_END
CREATE TABLE crowd_rules (
crowd_code VARCHAR(50) PRIMARY KEY,
crowd_name VARCHAR(100) NOT NULL,
rule_json TEXT NOT NULL, -- JSON formatted rule: {"logic":"AND", "conditions":[{"tag_code":"age","op":">","value":18},...]}
schedule_cron VARCHAR(50), -- e.g., "0 0 2 * * ?" for daily 2 AM
last_calc_dt DATE,
user_count BIGINT DEFAULT 0,
status VARCHAR(20) DEFAULT 'active', -- 'active', 'paused'
create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

HBase 表结构 (user_crowd_mapping)：

表名： user_crowd_mapping

RowKey： UID (建议加盐处理，如 hash(UID)%10_UID，防止热点)

列族： cf (column family)

列： crowd_code1 -> 1 (或计算时间戳), crowd_code2 -> 1, ...

Spark 人群计算伪代码 (PySpark)：

code
Python
download
content_copy
expand_less
IGNORE_WHEN_COPYING_START
IGNORE_WHEN_COPYING_END
# crowd_code, rule_json, dt = params from Airflow
from pyspark.sql import SparkSession
spark = SparkSession.builder.appName(f"CrowdCalc_{crowd_code}").enableHiveSupport().getOrCreate()

# Assuming rule_json is parsed into SQL WHERE clause
where_clause = parse_rule_json_to_sql_where(rule_json)

# Read DWS wide table
df_tags = spark.table("dws.user_profile_wide").filter(f"dt = '{dt}'")
df_crowd_uids = df_tags.filter(where_clause).select("uid").distinct()

# Export to Hive
df_crowd_uids.write.mode("overwrite").saveAsTable(f"ads.crowd_uid_{crowd_code}")

# Export to HBase (using spark-hbase-connector or prepare for HBase bulkload)
# Example for HBase direct put (simplified, for bulkload, generate HFiles)
def write_to_hbase(partition):
# HBase connection setup
# table.put(Put(uid_bytes).addColumn(cf_bytes, crowd_code_bytes, "1".encode()))
pass
df_crowd_uids.foreachPartition(write_to_hbase)

# Update crowd_rules table in PostgreSQL with user_count and last_calc_dt

2.3 活动平台

目标： 管理营销活动，关联人群并展示活动效果。

核心功能：

活动配置 (UI + PostgreSQL)：

运营人员创建活动，选择目标人群 (crowd_code)、活动时间 (start_time, end_time)、投放渠道（短信、Push、广告平台等）。

活动配置信息存储到 PostgreSQL 的 marketing_activity 表。

活动执行：

离线执行： 活动开始前，从 Hive/HBase 导出人群 UID 列表，批量同步到下游投放渠道。

实时触发： Flink 实时链路监测用户行为，当用户满足特定条件时（例如访问某页面），结合 HBase 中用户所属人群信息，实时触发精准营销（如弹窗、优惠券发放）。

活动效果看板 (Spark/Flink + Superset/Grafana)：

实时统计： Flink 消费 Kafka 中的活动触达/点击/转化事件，实时聚合指标，写入 PostgreSQL 或 Redis，供 UI 展示。

离线分析： Spark 定时任务聚合活动结束后的大盘数据，计算最终转化率、ROI 等，写入 Hive 或 PostgreSQL。

可视化： 通过 Superset/Grafana 对接 PostgreSQL/Hive/Redis，展示活动效果。

技术实现示例：

PostgreSQL 表结构 (marketing_activity)：

code
SQL
download
content_copy
expand_less
IGNORE_WHEN_COPYING_START
IGNORE_WHEN_COPYING_END
CREATE TABLE marketing_activity (
activity_id VARCHAR(50) PRIMARY KEY,
activity_name VARCHAR(100) NOT NULL,
crowd_code VARCHAR(50) REFERENCES crowd_rules(crowd_code),
channel VARCHAR(50), -- e.g., 'SMS', 'PUSH', 'AD'
start_time TIMESTAMP NOT NULL,
end_time TIMESTAMP NOT NULL,
status VARCHAR(20) DEFAULT 'planning', -- 'planning', 'running', 'ended'
total_reach_count BIGINT DEFAULT 0,
conversion_count BIGINT DEFAULT 0,
create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

2.4 人群匹配平台

目标： 提供高并发、低延迟的实时用户所属人群查询服务。

核心功能：

实时匹配：

接收外部系统传入的 UID，查询 HBase 的 user_crowd_mapping 表。

根据 UID 获取用户所属的所有人群 crowd_code 列表，或判断是否属于特定 crowd_code。

毫秒级响应，满足实时决策需求。

实时标签更新 (Flink)：

Flink 消费 Kafka 的用户行为日志。

实时计算轻量级标签（如“最近1小时活跃”、“是否在线”）。

将实时标签更新到 HBase 的 user_realtime_tags 表（RowKey=UID, Column=tag_code -> tag_value）。

Flink 监听 crowd_rules 变更，或定时刷新规则，动态判断用户是否满足实时人群条件，并更新 user_crowd_mapping。

API 服务 (Spring Boot)：

提供 RESTful API 接口：

GET /match?uid={uid}：返回 UID 所属的所有 crowd_code 列表。

GET /match?uid={uid}&crowdCode={crowd_code}：返回 UID 是否属于指定人群（true/false）。

后端服务直接访问 HBase 进行查询，可引入 Redis 缓存热点 UID 或人群匹配结果。

技术实现示例：

HBase 表结构 (user_realtime_tags)：

表名： user_realtime_tags

RowKey： UID

列族： rt (real-time)

列： rt_active_status -> online, rt_last_click_ts -> 1678886400, ...

Flink 实时更新伪代码 (Java/Scala)：

code
Java
download
content_copy
expand_less
IGNORE_WHEN_COPYING_START
IGNORE_WHEN_COPYING_END
// Flink DataStream API
DataStream<String> kafkaSource = env.fromSource(kafkaConsumer, WatermarkStrategy.noWatermarks(), "Kafka Source");

kafkaSource.map(event -> parseEvent(event))
.keyBy(event -> event.getUid())
.process(new ProcessFunction<UserEvent, Void>() {
// Keep KeyedState for real-time tags and crowd conditions
// On event arrival, update user's state (e.g., click count in last 1 hour)
// Check if user's state now matches any real-time crowd rule
// If yes, put into HBase user_crowd_mapping or user_realtime_tags
})
.addSink(new HBaseSinkFunction()); // Custom sink to write to HBase

API 服务伪代码 (Spring Boot)：

code
Java
download
content_copy
expand_less
IGNORE_WHEN_COPYING_START
IGNORE_WHEN_COPYING_END
@RestController
@RequestMapping("/api/crowd")
public class CrowdMatchController {

    @Autowired
    private HBaseService hbaseService; // Custom service for HBase operations

    @GetMapping("/match")
    public ResponseEntity<CrowdMatchResult> matchCrowd(@RequestParam String uid, @RequestParam(required = false) String crowdCode) {
        if (crowdCode != null) {
            boolean isMatch = hbaseService.isUserInCrowd(uid, crowdCode);
            return ResponseEntity.ok(new CrowdMatchResult(uid, crowdCode, isMatch));
        } else {
            List<String> userCrowds = hbaseService.getUserCrowds(uid);
            return ResponseEntity.ok(new CrowdMatchResult(uid, userCrowds));
        }
    }
}
三、 部署与运维

集群规划：

Hadoop 集群： HDFS (NameNode HA, DataNodes), YARN (ResourceManager HA, NodeManagers), Zookeeper。

Hive 集群： Hive Metastore (对接 PostgreSQL), HiveServer2。

Kafka 集群： 至少 3 个 Broker，多分区、多副本。

HBase 集群： RegionServers, HMaster (HA), Zookeeper。

Spark 集群： Standalone, YARN 或 Kubernetes。

Flink 集群： JobManagers, TaskManagers (可部署在 YARN 或 Kubernetes)。

PostgreSQL： 主从复制或云数据库服务。

API 服务： 部署在 Kubernetes 或独立服务器。

监控告警：

资源监控： Prometheus + Grafana 监控 CPU、内存、磁盘 I/O、网络流量、JVM 指标等。

服务监控： Kafka Lag (Consumer Group 延迟)、Flink Job 状态、Spark Job 成功率与耗时、HBase QPS/延迟、API 响应时间/错误率。

告警： 基于监控指标，通过 Alertmanager 集成邮件、短信、钉钉等通知渠道。

调度系统： Airflow DAGs 管理所有离线 ETL、标签计算、人群计算、数据质检任务，支持依赖管理、重试机制、任务日志。

日志管理： ELK Stack (Elasticsearch, Logstash, Kibana) 或 Loki + Grafana 进行日志收集、存储、查询与可视化。

容灾设计：

HDFS NameNode HA，数据多副本存储。

Kafka 多 Broker，数据多副本。

HBase RegionServer HA，数据多副本。

Flink Checkpointing/Savepoints 机制确保数据一致性和故障恢复。

数据库主从复制。

安全控制：

数据权限： Apache Ranger 对 Hive、HDFS、HBase 进行细粒度权限控制。

API 安全： OAuth2/JWT 进行 API 认证与授权。

数据加密： 敏感数据存储加密，传输加密（TLS/SSL）。

审计： 记录关键操作日志，可追溯。

四、 扩展性优化

性能优化：

Hive： 合理分区裁剪、文件格式优化 (Parquet/ORC)、小文件合并、查询优化 (CBO)。

Spark： 资源动态分配、数据倾斜处理 (Salting)、广播变量、Join 优化、缓存。

HBase： RowKey 设计 (加盐/Hash)、预分区、Bloom Filter、Block Cache 优化。

Flink： State Backend 优化、Exactly-Once 语义、Watermark 与窗口函数。

功能扩展：

人群 Look-Alike： 基于现有高质量人群，利用 Spark MLlib 训练模型，发现潜在相似用户。

人群洞察： 基于圈定人群的标签分布，生成人群画像报告。

AB Testing： 引入 AB Test 平台，支持对不同人群进行差异化营销测试。

实时推荐： 结合实时用户行为、实时标签和人群信息，提供个性化推荐。

架构升级：

Data Lakehouse： 考虑引入 Apache Iceberg/Hudi 等，提升 Hive 的事务支持、Schema 演进和 Upsert 能力。

Feature Store： 统一管理离线和实时特征，供机器学习模型使用。

五、 总结

该方案构建了一个基于 Hive + Spark 的离线标签与人群计算核心，辅以 Flink + Kafka 实现实时标签更新与匹配，并利用 HBase 提供低延迟查询能力，PostgreSQL 管理业务元数据。它覆盖了从数据入仓、标签构建、人群规则定义、人群计算、活动关联到实时匹配和效果追踪的全流程，是一个可扩展、高性能、支持高并发实时场景与大规模离线分析的综合性大数据圈人营销平台。