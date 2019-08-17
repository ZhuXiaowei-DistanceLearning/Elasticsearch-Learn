---
typora-copy-images-to: Elasticsearch
---

# Elasticsearch

## 1.1 定义

![1565406773109](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565406773109.png)

### 1.1.1 逻辑设计

![1565406850967](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565406850967.png)

1. 文档(mongod,即某行某列的值,无模式)
   - 索引和搜索数据的最小单位是文档
   - 一篇文档同时包含字段和值
   - 层次型：文档中可以包含新的文档
   - 灵活性：不依赖于预先定义的字段
   - 通常是数据的JSON表示
   - 每个字段的类型很重要，可以是字符串，数组等等
2. 类型
   - 有时称为映射类型
   - 类型是文档的逻辑容器，类似于表格是行的容器
   - 每个类型中字段的定义称为映射
   - 类型是elasticsearch的概念，不属于Lucene
   - 类型包含了映射中的每个字段的定义。映射包括了该类型的文档中可能出现的所有字段
   - ![1565423530050](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565423530050.png)
3. 索引
   - 索引是映射类型的容器
   - 索引存储了所有映射类型的字段
   - 索引是由一个或多个分片的数据块组成
   - 索引是数据库，类型是数据库的表

### 1.1.2 物理设计

1. 定义

   - ![1565409791003](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565409791003.png)
   - 每个索引由5个主要分片组成，而每份主要分片又有一个副本，一共10份分片(新版本后为1个副本)

2. 节点

   - 一个节点是一个Elasticsearch实例。在服务器上启动Elasticsearch之后，你就拥有了一个节点
   - 多个节点可以组成一个集群

3. 分片

   1. 一份分片是Lucene的索引

   2. 分片是把一个大的索引分成多份放到不同的节点上

      - ![1565410129450](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565410129450.png)

   3. 索引由多个主分片以及零个或多个副本分片构成

      - ![1565410355764](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565410355764.png)

   4. 主分片
   
   1. 运行时不可以进行添加和移除
      2. 索引:get-together，分片为get-together0

      - ![1565410181696](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565410181696.png)

   5. 副分片
   
      1. 副分片是主分片的完整副本
   2. 副本分片用于搜索，或是在原有主分片丢失后成为新的主分片
      3. 运行时可以进行添加和移除

   6. 在集群中分发分片
   
      1. 一台机器运行着一个Elasticsearch进程，就已经建立了一个拥有单节点的集群
      2. 水平扩展：在节点中加入更多节点(请求分发，负载均衡)
      3. 垂直扩展：为Elasticsearch增加更多硬件资源
      4. ![1565410555996](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565410555996.png)

## 1.2 基本操作

### 1.2.1 创建索引和映射类型

1. 创建索引

   1. 创建索引比创建一篇文档要花费更多时间

   2. 

      ```java
      Put:http://localhost:9200/new_index
      结果：
      {
          "acknowledged": true,
          "shards_acknowledged": true,
          "index": "new_index"
      }
      ```

2. 获取映射

   1. 映射是随着新文档而自动创建

   2. http://地址/索引(get-together)/_mapping/类型(group)

   3. http://地址/索引(get-together)/类型(group)/_mapping

      ```java
      http://localhost:9200/get-together/_mapping/group
      结果:
      {
          "get-together": {
              "mappings": {
                  "group": {
                      "properties": {
                          "name": { // 属性列表
                              "type": "text",
                              "fields": {
                                  "keyword": {
                                      "type": "keyword", // 属性选项
                                      "ignore_above": 256
                                  }
                              }
                          },
                          "organizer": {
                              "type": "text",
                              "fields": {
                                  "keyword": {
                                      "type": "keyword",
                                      "ignore_above": 256
                                  }
                              }
                          }
                      }
                  }
              }
          }
      }
      ```

### 1.2.2 搜索并获取数据

1. 

2. ```java
   http://localhost:9200/get-together/group/_search?q=elasticsearch
   ```

3. 同时搜索

   ```java
   http://localhost:9200/get-together/group,event/_search?q=elasticsearch(用,分割)
   
   http://localhost:9200/get-together/_search?q=elasticsearch(在某个索引的多个类型中搜索)
   
   http://localhost:9200/get-together,other-index/_search?q=elasticsearch(在多个索引中搜索，并且,分割) // 如果other-index不存在，请求失败，可以设置ignore_unavailable
   ```

### 1.2.3 搜索回复

1. ![1565411724157](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565411724157.png)

2. 时间

   1. ```json
      {
          "took":2, // 花了多久处理请求，单位毫秒
      	"timed_out":false // 搜索是否超时,默认永远不会超时,可以在地址中设置url/?xx=xx&timeout=3s,如果超时了，该值为true
      }
      ```

3. 分片

   1. ```json
      {
          "_shards":{
              "total":2, // 总数
              "successful":2, // 成功数量
              "failed":0 // 失败数量
          }
      }
      ```

   2. ![1565412072823](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565412072823.png)

4. 命令统计数据

   1. ```json
      {
          htis:{ // 包含了匹配文档的数组
              "total":2, // 匹配总数
              "max_score":0.9066504 // 匹配文档的最高分,默认通过TF-IDF(词频-逆文档频率)算法
          }
      }
      ```

5. 结果文档

   1. ```json
      {
          "hits":{
              hits:[{
                  "_index":"get-together",
                  "_type":"group",
                  "_id":"3",
                  "_score":0.9066504,
                  "fields":{
                      "location":["San Francisco,California","USA"]
                      "name":["Elasticsearch San Francisco"]
                  }
              }]
          }
      }
      ```



### 1.2.4 如何搜索

1. ```json
   http://localhost:9200/get-together/_search/
   {
   	"query":{
   		"query_string":{ // 运行一个类型为query_string的查询
   			"query":"Denver",
              	"default_field":"name", // 默认查询_all字段(即所有字段)，指定字段
               "default_operaotr":"AND" // 默认操作符是OR，AND匹配所有的关键词
   		}
           // 结果同上
           "query":"name:elasticsearch AND name:san AND name:francisco",
           // 在name字段中只查找elasticsearch一个词
           "trem":{  
           "name":"elasticsearch"
       }
   	}
   }
   ```

2. ```json
   http://localhost:9200/get-together/_search/
   {
   	"query":{
           // 过滤
   		"filtered":{
               "filter":{
                   "term":{
                       "name":"elasticsearch"
                   }
               }
           }
   	}
   }
   ```

3. 应用聚集

   1. 词条聚集(terms aggregation)

   2. ```json
      {
          "aggregations":{
              "organizers":{
                  "trems":{ // 类型是terms,并且查找organizer字段
                      "field":"organizer"
                  }
              }
          }
      }
      ```

4. 通过ID获取文档

   1. ```
      // 通过ID获取文档要比搜索更快，所耗资源成本也更多
      http://localhost:9200/get-together/group/1
      ```

## 1.3 配置Elasticsearch

1. 在elasticsearch.yml中指定集群的名称

   1. 默认情况下，新的结点通过多播发现已有的集群，通过向所有主机发送ping请求，这些主机侦听某个特定的多播地址。如果发现新的集群而且有同样的集群名称，新的节点就会将加入他们。你需要定制化集群的名称，防止默认配置的实例加入到你的集群

   2. 在yml中解除cluster.name这注释。修改为![1565415049554](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565415049554.png)

      ```
      cluster.name:elasticsearch-in-action
      // 重启过后没有任何数据了，因为数据存储的目录包含集群的名称，可以将集群名称改回去然后再次重启，找回之前索引的数据
      ```

2. 通过logging.yml指定详细日志记录(使用log4j)

   1. 主要日志(cluster-name.log):运行时所发生一切的综合信息

   2. 慢搜索日志(cluster-name_index_search_showlog.log):当某个查询很慢时,Elasticsearch在这里进行记录。默认情况下，如果一个查询花费的时间多于半秒，将在这里写入一条记录

   3. 慢索引日志(cluster-name_index_indexing_slowlog.log)：默认情况下，如果一个索引操作花费的时间多于半秒，将在这里写入一条记录。

   4. ![1565415597231](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565415597231.png)

      ```
      默认为info
      rootLogger:TRACE,console,file
      ```

3. 调整JVM设置

   1. Elasticsearch使用的大部分内存称为堆(heap)。默认的设置让Elasticsearch为堆分配了256MB初始内存，然后最多扩展到1GB，如果超过1GB，则操作失败，日志记录out-of-memory错误

   2. ```
      // 在命令行上
      SET ES_HEAP_SIZE=500m & bin\elasticsearch.bat
      // 永久方式:在启动脚本上设置#!bin/sh ES_HEAP_SIZE=500m
      ```

4. 在集群中加入节点

   1. ![1565416026555](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565416026555.png)

## 1.5 索引操作

1. 新建索引

   1. ```java
      PUT:http://localhost:9200/bolg // 不能为大写字母
      ```

2. 设置初始化值

   ```java
   PUT:http://localhost:9200/bolg1
   {
   	“settings”:{
   	"number_of_shards":3 // 分片数量,该参数只能在初始化时使用
   	"number_of_replicas":0 // 副本数量
   	}
   }
   ```

3. 更新副本

   ```java
   PUT:http://localhost:9200/bolg1/_settings
   {
   	“settings”:{
   	"number_of_replicas":0 // 副本数量
   	}
   }
   ```

4. 读写权限

   ```java
   blocks.read_only:true
   blocks.read:true
   blocks.write:true // 禁止对当前索引的写操作
   PUT:http://localhost:9200/bolg1/_settings
   {
   	"settings":{
   		"blocks.write":true
   	}
   }
   ```

5. 查看索引

   ```java
   GET:http://localhost:9200/bolg/_settings
   GET:http://localhost:9200/_all/_settings
   ```

6. 删除索引

   ```java
   DELETE:http://localhost:9200/bolg1
   ```

7. 索引的打开与关闭

   ```java
   POST:http://localhost:9200/bolg/_open
   POST:http://localhost:9200/bolg/_close
   POST:http://localhost:9200/_all/_close // 匹配所有索引
   POST:http://localhost:9200/test*/_close // 匹配test开头的索引
   POST:http://localhost:9200/bolg/_close?ignore_unavailable=true // 忽略不存在的索引
   ```

8. 复制索引

   ```json
   POST:http://localhost:9200/bolg/reindex
   {
       "source":{
           "index":"bolg",
           "type":"article",
           "query":{
               "term":{"title":"git"}
           }
       },
       "dest":{
           "index":"bolg_news"
       }
   }
   ```

9. 收缩索引

   1. 一个索引的分片初始化以后是无法再做修改的，但可以使用shrink index AP提供的缩小索引分片数机制，但是收缩后的分片数必须是原始分片数的因子。例如：8,收缩为2,4,1
   2. 缩小索引前，索引必须被标记为只读，所有分片都会复制到一个相同的结点并且结点健康值为绿色的。
   3. ![1565427560741](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565427560741.png)

10. 索引别名

    ```json
    POST:http://localhost:9200/_aliases
    {
    	"actions":[
    		{
    			"add":{ // 增加别名
    				"index":"bolg",
    				"alias":"alias1"
    			}
    		},
            {
    			"remove":{ // 移除别名
    				"index":"bolg",
    				"alias":"alias1"
    			}
    		}
    		],
        "actions":[ // 另一种形式的写法
            {"add":{
                "indices":
                ["test1","test2"],
                "alias","alias1"}}
        ]
    }
    GET:http://localhost:9200/blog/_aliases // 获取该索引别名
    GET:http://localhost:9200/_aliases // 获取所有别名
    ```

## 1.6 文档操作

1. 新建文档

   ```json
   PUT:http://localhost:9200/blog/article/1
   {
   	"title":"git简介",
   	"posttime":"2017-05-01",
   	"content":"git是一款免费开源的软件"
   }
   POST:http://localhost:9200/blog/article // 不指定id则自动生成
   ```

2. 获取文档

   ```json
   GET:http://localhost:9200/blog/article/1
   HEAD:http://localhost:9200/blog/article/1 // 判断文档是否存在，如果存在则返回200
   ```

3. 更新文档

   ```json
   PUT:http://localhost:9200/blog/article/1
   {
   	"counter":1,
   	"tags":["red"]
   }
   POST:http://localhost:9200/blog/article/1/_update // 将counter值变为5
   {
   	"script":{
   		"inline":"ctx._source.counter += params.count", // 执行的脚本,ctx是脚本语言中的一个执行对象,ctx获取_source在修改counter字段
   		"lang":"painless", //更新文档
   		"params":{
   			"count":4
   		}
   	}
   }
   ```

   ```java
   POST:http://localhost:9200/blog/article/1/_update
   {
   	"script":"ctx._source.new_fiel = 'value_of_new_field'" // 新添一个字段
   	"script":"ctx._source.remove('value_of_new_field')" // 移除一个字段
   }
   ```

4. 查询更新

   ```java
   POST:http://localhost:9200/blog/_update_by_query
   {
   	"script":{
   	"inline":"ctx._source.category = params.category",
   	"lang":"painless",
   	"params":{"category":"git"}
   	},
   	"query":{
   		"term":{"title":"git"}
   	}
   }
   ```

5. 删除文档

   ```java
   DELETE:http://localhost:9200/blog/article/1
   POST:http://localhost:9200/blog/_delete_by_query //查询删除
   {
   	"query":{
   		"term":{
   			"title":"git"
   		}
   	}
   }
   {
   	"query":{
   		"match_all":{} // 删除一个type下的所有文档
   	}
   }
   ```

6. 批量操作

   1. 通过Bulk API可以换行批量索引、批量删除、批量更新等操作

   2. 创建一个JSON文件

   3. 文件中写入多个请求操作，格式如下：

      action_and_meta_data\n // 指定了将要在哪个文档中执行什么操作(action必须是index、create、update或者delete。metadata需要指明需要被操作文档的_index、_type以及_id)

      optional_source\n

      action_and_meta_data\n

      optional_source\n

   4. 执行操作

      ```json
      POST:localhost:9200/indexname/_bulk?pretty --data-binary@accounts.json
      {
      	"index":{
              "_index":"blog",
              "_type":"article",
              "_id":"1"
          },
      	"create":{
              "_index":"blog",
              "_type":"article",
              "_id":"1"
          },
      	"title":"blog title"
      }
      ```

   5. 版本控制

      1. version

   6. 路由机制

      1. ```json
         POST:http://localhost:9200/website/blog/1?routing=user123
         {
             "title":"xxx",
             "name":"xxx"
         }
         GET:http://localhost:9200/website/blog/1?routing=user123
         ```

## 1.7 映射操作

1. 定义

   1. 用来定义一个文档以及其所包含的字段如何被存储和索引，可以在映射中事先定义字段的数据类型、分词器等属性

2. 动态映射

   1. 文档中字段的类型是Elasticsearch自动识别的，不需要在创建索引的时候设置字段的类型。

   2. ![1565441699052](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565441699052.png)

   3. ```json
      http://localhost:9200/books/_mapping
      mapping设置
      true:默认值为true，自动添加字段
      false:忽略新的字段
      strict:严格模式，发现新的字段抛出异常
      {
          "mapping":{
              "my_type":{
                "data_detection":false // 忽略日期设置，总会被当做string类型，如要需要新增一个date类型的字段，需要手动添加 
              },
              "it":{
                  "dynamic":"strict",
                  "properties":{
                      "title":{
                          "type":"text"
                      },
                      "publish_date":{
                          "type":"date"
                      }
                  }
              }
          }
      }
      ```

   4. 日期检测

3. 静态映射

   1. 创建索引时手工指定索引映射

   2. ```json
      { // 手动指定类型
      	"mappings":{
      		"user":{
      			"_all":{"enable":false},
      			"properties":{
      				"title":{
      					"type":"text"
      				},
      				"name":{
      					"type":"text"
      				},
      				"age":{
      					"type":"integer"
      				}
      			},
      			"blogpost":{
      				"_all":{"enable":false},
      				"properties":{
      					"title":{
      					"type":"text"
      				},
      				"body":{
      					"type":"text"
      				},
      				"user_id":{
      					"type":"keyword"
      				},
      				"create":{
      					"type":"date",
      					"format":"stirict_date_optional_time||epoch_millis"
      				}
      				}
      			}
      		}
      	}
      }
      ```

   3. ![1565442350443](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565442350443.png)

   4. string:Elasticsearch 5.X之后的字段类型不再支持string,由text或keyword取代

   5. text:如果一个字段要被全文搜索，应该使用text类型。设置text类型以后，字段内容会被分析，在生成倒排索引以前，字符串会被分词器分成一个一个词项。text类型的字段不用于排序，很少用于聚合(termsAggregation除外)

   6. keyword:适用于索引结构化的字段。通常用于过滤、排序、聚合。只能通过精确值搜索到

   7. 数字类型：![1565442559670](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565442559670.png)

   8. date

      1. 格式化日期的字符串
      2. 代表milliseconds-since-the-epoch的长整形数
      3. 代表seconds-since-the-epoch的整形数
      4. 内部会把日期转换为UTC(世界标准时间)

   9. boolean:5.4版本之后可接受的值为true、false、"true"、"false"

   10. binary:接受base64编码的字符串，默认不存储(这里的存储是指store属性取值为false)

   11. array:默认情况下任何字段都可以包含一个或者多个值，但是一个数组中的字必须是同一类型

       ```json
       {
           "tags":["elasticsearch"."wow"],
           "list":[
               {
                   "name":"prog_list",
                   "description":"cool stff list"
               },
                {
                   "name":"prog_list",
                   "description":"cool stff list"
               }
           ]
       }
       ```

   12. object:对象中可以包含对象(扁平化)

       ```json
       {
           "region":"CHINA",
           "manager.age":30
       }
       { 
           "my_type":{
               "properties":{
                   "region":{
                       "type":"keyword"
                   }
               },
               "manager":{
                   "properties":{
                       "age":{"type":"interger"}
                   }
               }
           }
       }
       ```

   13. nested:Object类型的一个特例，可以让对象数组独立索引和查询

       ```json
       {
           "user":[
               {
                   "first":"Jhon",
                   "last":"Smith"
               },
                {
                   "first":"Alice",
                   "last":"White"
               }
           ]
       }
       {
           "user.first":["alice","Jhon"] // 扁平化
       }
       ```

   14. geo point

       1. 存储地理位置信息的经纬度

       2. 查找一定范围内的地理位置

       3. 通过地理位置或者相对中心店的距离来聚合文档

       4. 把距离因素整合到文档的评分中

       5. 通过距离对文档排序

       6. ```json
          {
              "location":{
                  "lat":41.12,
                  "lon":-71.34
              }
          }
          ```

   15. ip

       1. 存储IPv4或者IPv6，在映射中指定字段为ip类型的迎合和查询语句如下

          ```json
          {
              "ip_addr":{"type":"ip"}
          }
          ```

   16. range

       1. ![1565444423689](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565444423689.png)

       2. ```json
          {
          	"type":"date_range"	
          }
          {
              "get":"2015-10-31 12:00:00"
          }
          ```

   17. token_count

       1. 用于统计字符串分词后的词项个数，本质上是一个整数型字段

   18. 元字段

       1. ![1565444584416](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565444584416.png)

       2. _field_names

          ```json
          {
              "query":{
                  "terms":{
                      "_field_names":{"body"}
                  }
              }
          }
          ```

       3. _parent:用于指定同一索引中文档的父子关系。

          ```json
          {
              "mappings":{
                  "my_parent":{},
                  "my_child":{
                      "_parent":{
                          "type":"my_parent"
                      }
                  }
              }
          }
          PUT:my_index/my_child_3?parent:1&refresh=true
          ```

       4. _routing

## 1.8 映射参数

1. analyzer(索引分词器)

   1. 用于指定文本字段的分词器，对索引和查询都有效。分词器会把文本类型的内容转换为若干个词项，查询时分词器同样把查询字符串通过和索引时期相同的分词器或者其他分词器进行解析

   2. 对于content字段，analyzer参数的取值为ik_max_word，意味着content字段内容索引时和查询时搜使用ik_max_word分词

      ```json
      {
      	"mappings":{
              "my_type":{
                  "properties":{
                      "content":{
                          "type":"text",
                          "analyzer":"ik_max_word"
                      }
                  }
              }
          }
      }
      ```

2. search_analyzer(搜索分词器)

   1. ```json
      {
          "search_analyzer":"strandard" //  一般索引和分词用相同的分词器
      }
      ```

3. normalizer（标准化配置）

   1. ```json
      {
          "settings":{
              "analysis":{
                  "normalizer":{
                      "my_normalizer":{
                          "type":"custom",
                          "char_filter":[],
                          "filter":["lowercase","asciifolding"]
                      }
                  }
              }
          }
      }
      ```

4. boost（用于设置字段权重）

   1. ```json
      {
          "title":{
              "type":"text",
              "boost":2
          },
          "query":{
              "match":{
                  "title":{
                      "query":"quick brown fox",
                      "boost":2
                  }
          }
      }
      ```

5. coerce(清除脏数据，默认为true)

   1. 整形数字5有可能会被携程字符串"5"，类型强制转换

6. copy_to(用于自定义_all，可以把多个字段的值复制到一个超级字段)

   1. ```json
      {
          "copy_to":"full_content",
          "full_content":{
              "type":"text"
          }
      }
      ```

7. doc_values(加快排序、聚合操作)

   1. ```json
      {
      	"properties":{
              "status_code":{
                  "type":;"keyword",
                  "doc_values":false
              }
          }
      }
      ```

8. dynamic(检测新发现的字段)

9. enable(默认索引所有的字段)

   1. ```json
      {
          "mappings":{
              "my_type":{
                  "enable":false, // 禁用映射
                  "properties":{
                      "name":{
                          "enable":false // 禁用索引
                      }
                  }
              }
          }
      }
      ```

10. fielddata
    1. 搜索要解决的问题是"包含查询关键词的文档有哪些"
    2. 聚合要解决的问题是"文档包含哪些词项"
    3. 大多数字段在索引时都会生成doc_values,text字段除外
    4. text字段在查询时会生成一个fielddata的数据结构
    5. fielddata在字段首次被聚合、排序或者使用脚本的时候生成

11. format(指定日期格式)

    1. ```json
       {
           "formate":"yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || epoch_millis"
       }
       ```

    2. ![1565447038751](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565447038751.png)

       ![1565447059374](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565447059374.png)

       ![1565447069890](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565447069890.png)

       ![1565447077699](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565447077699.png)

12. ignore_above

    1. 指定字段分词和索引的字符串最大长度，超过最大值的会被忽略

13. ignore_malformed

    1. 忽略不规则数据

14. include_in_all

    1. 指定字段的值是否包含在_all字段中，默认为true

15. index

    1. 指定字段是否索引，不索引也就不可搜索

16. index_options

    1. 控制索引时存储哪些信息到倒排序索引中
    2. ![1565447218878](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565447218878.png)

17. fields

    1. 可以让同一字段有多种不同的索引方式

18. norms

    1. 标准化文档，以便查询时计算文档的相关性

19. null_value

    1. 值为null的字段不索引也不可以搜索
    2. 可以让值为null的字段显示的可索引、可搜索

20. postion_increment_gap

    1. 为了支持近似或者短语查询，text类型的字段被解析的时候会考虑词项的位置信息。

21. properties

    1. 类型的映射、普通字段、object类型和nested类型的字段都称为properties(属性)

22. similarity

    1. 指定文档评分模型
    2. BM25:Elasticsearch和Lucene默认的评分模型
    3. classic:TF/IDF评分
    4. boolean

23. store

    1. 字段是被索引的，也可以搜索，但是不存储

24. term_vector

    1. 词项集合
    2. 词项位置
    3. 词项的起始字符映射到原始文档中的位置
    4. ![1565448058612](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1565448058612.png)

## 2.1 搜索详解

1. 创建索引

   ```json
   {
   	"settings":{
   		"number_of_replicas":1,
   		"number_of_shards":3
   	},
   	"mappings":{
   		"IT":{
   			"properties":{
   				"id":{
   					"type":"long"
   				},
   				"title":{
   					"type":"text",
   					"analyzer":"ik_max_word"
   				},
   				"language":{
   					"type":"keyword"
   				},
   				"author":{
   					"type":"keyword"
   				},
   					"price":{
   					"type":"double"
   				},	"year":{
   					"type":"date",
   					"format":"yyy-MM-dd"
   				},	"description":{
   					"type":"text",
   					"analyzer":"ik_max_word"
   				}
   			}
   		}
   	}
   }
   ```

2. match_all_query

   1. 返回所有文档，文档得分都是1。简写形式：_search

3. term

   1. 查询字段中包含给定单词的文档，term查询不被解析，只有查询词和文档中的词精确匹配才会被搜索到

   2. 查询的是词项，例如：java编程思想，分割为java,"编程","思想"，java编程则无法查出

   3. 应用场景为查询人名、地名等精确匹配需求

   4. from：指定返回结果的开始位置，size：指定返回结果包含的最大文档数

      ```json
      {
          "from":0,
          "size":100,
          "_source":["title","author"], // 只返回某些字段
          "version":true, // 默认不返回版本号，需要指定
          "min_score"0.6, // 指定最小评分
      	"query":{
      		"term":{
      			"title":"思想" // 查询 title中含有“思想”关键字的书籍
      		}
      	},
      	"highlight":{ // 高亮查询关键字
              "fields":{
                  "title":{}
              }
          }
      }
      ```

4. match

   1. ```json
      {
      	"query":{
      		"match":{
      			"title":{
                  	"query":"java编程思想",
                      "operator":"or"
                  }
      		}	
      	}
      }
      ```

5. match_phrase

   1. 首先会把query内容分词，分词器可以自定义，同时需要满足以下两个条件才会被搜索到

      1. 分词后所有词项都要出出现在该字段中

      2. 字段中的词顺序要一致

      3. ```json
         {
         	"query":{
         		"match_phrase":{
                     "foo":"hello world"
                 }
         	}
         }
         ```

6. multi_match

   1. 用于搜索多个字段

      ```json
      {
      	"query":{
      		"multi_match":{
                 "query":"java编程",
                  "fields":["titile","description"]
              },
              "multi_match":{
                 "query":"java编程",
                  "fields":["titile","*_name"] // 支持通配符匹配
              },
              "multi_match":{
                 "query":"java编程",
                  "fields":["titile^3","description"] // ^：声明权重
              }
      	}
      }
      ```

7. common_terms_query

   1. 在不牺牲性能的情况下替代停用词提高搜索准确率和召回率的方案

8. query_string_query

   1. 允许在一个查询语句中使用多个特殊条件关键字(AND|OR|NOT)对多个字段进行查询

9. simple_query_string

   1. 适合直接暴露给用户，并且具有非常完善的查询语法的查询语句，接收Lucene查询语句，解析过程中发生错误不会抛出异常

## 2.2 词项查询

1. terms query

   1. term的升级，可以用来查询文档中包含多个词的文档，比如想查询title字段中包含关键词"java"或"python"的文档

      ```json
      {
          "query":{
              "terms":{
                  "title":["java","python"]
              }
          }
      }
      ```

2. range query

   1. 用来匹配在某一范围内的数值型、日期类型或字符串字段的文档

   2. gt，gte：大于等于,lt,lte:小于等于

      ```json
      {
          "query":{
              "range":{
                  "price":{
                      "gt":50,
                      "lte":70
                  }
              }
          }
      }
      ```

3. exists query

   1. 查询会返回字段中至少有一个非空值的文档

      ```json
      {
          "query":{
              "exists":{
                  "field":"user"
              }
          }
      }
      // 以下文档会匹配
      {"user":"jane"}
      {"user":""}
      {"user":"-"}
      {"user":["jane"]}
      {"user":["jane",null]}
      // 以下不会匹配
      {"user":null}
      {"user":[jane]}
      {"user":[null]}
      ```

4. prefix query

   1. 查询某个字段中以给定前缀开始的文档，比如查询title中含有以java为前缀的关键词的文档

      ```json
      {
          "query":{
              "prefix":{
                  "description":"win"
              }
          }
      }
      ```

5. wildcard query

   1. 通配符查询

6. regexp query

   1. 正则表达式查询，例如：a.c.e和ab...都可以匹配abcde

7. fuzzy query

   1. 通过计算词项与文档的编辑距离来得到结果的，但是使用fuzzy查询需要消耗的资源比较大，查询效率不高，适用于需要模糊查询的场景。

8. type query

   1. 查询具有指定类型的文档

      ```json
      {
          "query":{
              "type":{
                  "value":"IT"
              }
          }
      }
      ```

9. ids query

   1. 用户查询具有指定id的文档，可以接受数组，如果未指定类型，则会搜索索引中的所有类型

      ```json
      {
          "query":{
              "ids":{
                  "type":"IT",
                  "values":["1","3","5"]
              }
          }
      }
      ```

## 2.3 复合查询

1. constant_score query

   1. 可以包装一个其他类型的查询，并返回匹配过滤器中的查询条件且具有相同评分的文档

      ```json
      {
          "query":{
              "constant_score":{
                  "filter":{ // 返回titile字段中含有关键词java的文档
                      "term":{"title":"java"}
                  },
                  "boost":1.2 // 所有文档的评分都是1.2
              }
          }
      }
      ```

2. bool query

   1. 任意多个简单查询组合在一起，使用must,should,must_not,filter选项来表示简单查询之间的逻辑，每个选项都可以出现0次到多次

      1. must：相当于逻辑运算AND
      2. should：相当于逻辑运算OR
      3. must_not
      4. filter：与must相同，但是不评分只起到过滤功能

   2. ```json
      {
          "query":{
              "bool":{
                  "minimum_should_match":1,
                  "must":{ 
                      "match":{"title":"java"}
                  },
                  "should":{ 
                      "match":{"title":"java"}
                  },
                  "must_not":{ 
                      "range":{"price":{"gte":70}}
                  }
              }
          }
      }
      ```

3. dis_max_query

   1. 支持多并发查询，可以返回与任意条件子句匹配的任何文档类型，与bool查询可以将所有匹配查询的分数相结合使的方式不同，dis_max查询只使用最佳匹配查询条件的分数

      ```json
      {
          "query":{
              "dis_max":{
                  "tie_breaker":0.7,
                  "boost":1.2,
                  "queries":[
                  {"term":{"age":34}}
                  ]
              }
          }
      }
      ```

4. functino_score_query

   1. 可以修改查询的文档得分，这个查询在有些情况下非常有用，比如通过评分函数计算文档得分代价较高，可以改用过滤器加自定义评分函数的方式来取代传统的评分方法

   2. 需要定义个查询和一至多个评分函数，评分函数会对查询到的每个文档分别计算得分

      ```json
      {
          "query":{
              "function_score":{
                  "query":{"match_all":{}},
                  "boost":"5",
                  "random_score":{},
                  "boost_mode":"multipay"
              }
          }
      }
      ```

5. boosting query

   1. 对两个查询的评分进行调整的场景，boosting查询会把两个查询封装在一起并降低其中一个查询的评分，

      ```json
      {
          "query":{
              "boosting":{
                  "positive":{
                      "match":{
                          "title":"python"
                      }
                  }
              }
          }
      }
      ```

6. indices query

   1. 用于在多个索引之间进行查询的场景，允许指定一个索引名字列表和内部查询。含有query和no_match_query两部分，query中用于搜索指定索引列表中的文档

## 2.4 嵌套查询

1. nested query(嵌套查询)

   1. 文档中可能包含嵌套类型的字段，这些字段用来索引一些数组对象，每个对象都可以作为一条独立的文档被查询出来

      ```json
      {
          "mappings":{
              "type1":{
                  "properties":{
                      "obj1":{
                          "type":"nested"
                      }
                  }
              }
          }
      }
      ```

2. has_child query(有子查询)

   1. 文档的父子关系创建索引时再映射中声明

      ```json
      {
          "mappings":{
              "branch":{},
              "emplyee":{
                      "_parent":{
                          "type":"branch"
                      }
                  }
              }
          }
      }
      ```

3. has_parent query(有父查询)

## 2.5 位置查询

1. ```json
   //创建索引
   put geo{
       "mappings":{
           "properties":{
               "name":{
                   "type":"keyword"
               }
           },
           "location":{
               "type":"geo_point"
           }
       }
   }
   ```

## 2.6 特殊查询

1. more_like_thie_query
   1. 可以查询和提供文本类似的文档，通常用于近似文本的推荐等场景

## 2.7 搜索高亮

1. 自定义高亮的字段：pre_tags和post_tags

## 2.8 搜索排序

1. 按照查询和文档的相关度进行排序，默认按评分降序排序

   ```json
   {
       "query":{
           "term":{
               "title":"java"
           }
       },
       "sort":[
           {"_score":{"order":"asc"}}
       ]
   }
   ```

## 3 指标聚合

1. 指标聚合

   1. max aggregation:统计最大值

      ```json
      {
          "size":0,
          "aggs":{
              "max_price":{
                  "max":{
                      "field":"price"
                  }
              }
          }
      }
      ```

   2. min aggregation

   3. avg aggregation

   4. sum aggregation

   5. cardinality aggregation

      1. 基数统计，先执行类似distinct操作，然后统计集合长度

   6. stats aggregation

      1. 基本统计，会一次返回count、max、min、avg和sum

   7. Extended stats aggregation

      1. 高级统计，在基本统计的基础上多4个结果，平方和、方差、标准差、平均值加/减两个标准差区别

   8. percentiles aggregation

      1. 百分比统计

   9. value count aggregation

      1. 统计文档数量

   10. terms aggregation

       1. 用于分组聚合

   11. filter aggregation

       1. 过滤器聚合，可以把符合过滤器中的条件的文档分到一个桶中

   12. filters aggregation

       1. 多个过滤器聚合，可以把符合多个过滤条件的文档分到不同的桶中

   13. range aggregation

       1. 范围聚合，用于反映数据的分布情况

   14. Date Range aggregation

       1. 日期类型的范围聚合，日期的起止值可以用数学表达式

   15. Date Histogram aggregation

       1. 时间直方图聚合，常用于按照日期对文档进行统计并绘制条形图

   16. Missing aggregation

       1. 空值聚合，可以把文档集中所有缺失字段的文档分到一个桶中

   17. Children aggregation

       1. 特殊的单桶聚合，可以根据父子文档关系进行分桶

   18. Geo Distance aggregation

       1. 对地理点做范围统计

   19. IP Range Aggregation

       1. 对IP类型数据范围聚合

## 4 JAVA API

1. 连接测试

   ```java
   package com.zxw.Test;
   
   
   import org.elasticsearch.action.get.GetResponse;
   import org.elasticsearch.client.transport.TransportClient;
   import org.elasticsearch.common.settings.Settings;
   import org.elasticsearch.common.transport.TransportAddress;
   import org.elasticsearch.transport.client.PreBuiltTransportClient;
   
   import java.net.InetAddress;
   import java.net.InetSocketAddress;
   import java.net.UnknownHostException;
   
   public class TestClient {
       public static String CLUSTER_NAME = "elasticsearch";
       public static String HOST_IP = "127.0.0.1";
       public static int TCP_PORT = 9300;
   
       public static void main(String[] args) throws UnknownHostException {
           // 指定集群名称
           Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
           //
           TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(HOST_IP), TCP_PORT));
           // 读取文档
           GetResponse response = client.prepareGet("books", "IT", "1").get();
           System.out.println(response.getSourceAsString());
       }
   }
   ```

2. 索引

   ```java
   package com.zxw.index;
   
   import com.carrotsearch.hppc.cursors.ObjectObjectCursor;
   import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsResponse;
   import org.elasticsearch.action.admin.indices.exists.types.TypesExistsResponse;
   import org.elasticsearch.action.admin.indices.settings.get.GetSettingsResponse;
   import org.elasticsearch.action.support.master.AcknowledgedResponse;
   import org.elasticsearch.client.IndicesAdminClient;
   import org.elasticsearch.client.transport.TransportClient;
   import org.elasticsearch.common.settings.Settings;
   import org.elasticsearch.common.transport.TransportAddress;
   import org.elasticsearch.transport.client.PreBuiltTransportClient;
   
   import java.net.InetAddress;
   import java.net.UnknownHostException;
   
   /**
    * 索引API
    */
   public class TextIndices {
       public static String CLUSTER_NAME = "elasticsearch";
       public static String HOST_IP = "127.0.0.1";
       public static int TCP_PORT = 9300;
   
       public static void main(String[] args) throws UnknownHostException {
           // 指定集群名称
           Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
           //
           TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(HOST_IP), TCP_PORT));
           IndicesAdminClient indices = client.admin().indices();
           // 判断索引是否存在
           IndicesExistsResponse books = indices.prepareExists("books").get();
           System.out.println(books.isExists());
           // 判断type是否存在
           TypesExistsResponse response = indices.prepareTypesExists("books").setTypes("type1", "type2").get();
           System.out.println(response.isExists());
           // 创建索引
   //        createIndex(indices);
           // 更新settings
   //        updateSettings(settings, indices);
           // 获取settings
   //        getSettings(settings, indices);
           // 设置mapping
           AcknowledgedResponse response1 = client.admin().indices().preparePutMapping("twitter").setType("tweet").setSource("{\"properties\":{\"name\":{" +
                   "\"type\":\"keyword\"}}}").get();
           System.out.println(response1);
       }
   
       private static void createIndex(IndicesAdminClient indices) {
           indices.prepareCreate("twitter").setSettings(Settings.builder().put("index.number_of_shards", 3).put("index.number_of_replicas", 2)).get();
       }
   
       private static void updateSettings(Settings settings, IndicesAdminClient indices) {
           // 更新副本
           indices.prepareUpdateSettings("twitter").setSettings(Settings.builder().put("index.number_of_replicas", 0)).get();
       }
   
       private static void getSettings(Settings settings, IndicesAdminClient indices) {
           // 获取Settings
           GetSettingsResponse getSettingsResponse = indices.prepareGetSettings("twitter").get();
           for (ObjectObjectCursor<String, Settings> indexToSetting : getSettingsResponse.getIndexToSettings()) {
               String index = indexToSetting.key;
               Settings value = indexToSetting.value;
               Integer shards = settings.getAsInt("index.number_of_shards", null);
               Integer replicas = settings.getAsInt("index.number_of_replicas", null);
               System.out.println(index);
               System.out.println(value);
               System.out.println(shards);
               System.out.println(replicas);
           }
       }
   }
   ```

3. 文档

   ```java
   package com.zxw.docu;
   
   import org.elasticsearch.action.get.GetResponse;
   import org.elasticsearch.action.index.IndexResponse;
   import org.elasticsearch.client.transport.TransportClient;
   import org.elasticsearch.common.settings.Settings;
   import org.elasticsearch.common.transport.TransportAddress;
   import org.elasticsearch.transport.client.PreBuiltTransportClient;
   
   import java.net.InetAddress;
   import java.net.UnknownHostException;
   import java.util.HashMap;
   import java.util.Map;
   
   public class TextDocument {
       public static String CLUSTER_NAME = "elasticsearch";
       public static String HOST_IP = "127.0.0.1";
       public static int TCP_PORT = 9300;
   
       public static void main(String[] args) throws UnknownHostException {
           Settings settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
           TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(new TransportAddress(InetAddress.getByName(HOST_IP), TCP_PORT));
           // 新建文档
   //        createDoc(client);
           GetResponse response = client.prepareGet("books", "_search",null).get();
           System.out.println(response.getSourceAsString());
       }
   
       private static void createDoc(TransportClient client) {
           Map<String, Object> doc2 = new HashMap<>();
           doc2.put("user", "kumchy");
           doc2.put("postDate", "2013-01-30");
           doc2.put("message", "trying out Elasticsearch");
           IndexResponse response = client.prepareIndex("books","it").setSource(doc2).get();
           System.out.println(response.status());
       }
   }
   ```

## 5 集群管理

### 5.1 集群规划

1. 为了避免节点各自为主节点，可以在Elasticsearch的配置文件(elasticsearch.yml)这是discovery.zen.minimum_master_nodes,这个参数决定了主节点选择过程中最少需要有多少个master节点，默认配置为1。一个基本原则需要设置成N/2+1,N是集群中节点的数量。例如在3个集群中应该设为2。
2. ![](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1566029630098.png)

### 5.2 索引规划

1. 默认情况下一个索引的分片数是5，副本数是1。分片是把一个大的索引分成多份放到不同的结点上来加速查询效率
2. 查询的响应时间受多个变量的影响
   1. ![1566030102327](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1566030102327.png)

### 5.3 分布式集群

1. master节点：master节点主要负责元数据的处理，比如索引的新增、删除、分片分配等，每当元数据有更新时，master节点负责同步到其他节点上
2. data节点：data节点上保存了数据分片。它负责数据相关操作，比如分片的增删改查以及搜索和整合操作
3. client节点：client节点起到路由请求的作用，实际上可以看作负载均衡器，适用于高并发访问的业务场景

### 5.4 Cat API

1. cat aliases:显示索引别名，也包括过滤器和路由信息

   ```
   localhost:9200/_cat/aliases?v
   ```

2. cat allocation：查看每个节点分片的分配数量以及它们所使用的硬盘空间大小

   ```
   localhost:9200/_cat/allocation?v
   ```

3. cat count：查询整个集群或者单个索引的文档数量

   ```
   localhost:9200/_cat/count?v
   ```

4. cat fielddata:查看当前集群中每个数据节点上

5. cat  health:显示集群的健康信息

6. cat indices：查看索引信息，包括索引健康状态、索引开关状态、分片数、副本数、文档数量、标记为删除的文档数量、占用的存储空间等信息

7. cat master：显示master节点的节点ID、绑定的IP和节点名。

8. cat nodeattrs：显示指定节点的属性信息

9. cat nodes：查看集群拓扑结构

10. cat pending tasks：查看正在执行的任务列表

11. cat plugins：查看每一个节点所运行插件的信息

12. cat recovery：一个索引分片恢复的视图，包括恢复中的和前线已完成的

13. cat repositories：展示集群中注册的快照库

14. cat thread pool：展示集群中每一个节点线程池的统计信息。默认情况下返回所有线程池的active、queue和rejected的统计信息

15. cat shards：查看节点包含的分片信息，包括一个分片是主分片还是一个副本分片、文档的数量、硬盘上占用的字节数、节点所在的位置等信息

16. cat segments：查看索引的段信息

17. cat templates:查看集群中的模板

### 5.5 Cluster API

1. cluster health:查看当前集群的健康信息

   ```json
   localhost:9200/_cluster/health
   {
       "cluster_name": "elasticsearch",
       "status": "yellow",
       "timed_out": false,
       "number_of_nodes": 1,
       "number_of_data_nodes": 1,// data节点数
       "active_primary_shards": 43,// 活动的主分片
       "active_shards": 43,// 所有活动的分片数
       "relocating_shards": 0,// 正在发生迁移的分片
       "initializing_shards": 0,// 正在初始化的分片
       "unassigned_shards": 45,// 没有被分配的分片
       "delayed_unassigned_shards": 0,// 延迟未被分配的分片
       "number_of_pending_tasks": 0,// 节点任务队列中的任务书
       "number_of_in_flight_fetch": 0,// 正在进行迁移的分片数量
       "task_max_waiting_in_queue_millis": 0,// 队列中任务的最大等待时间
       "active_shards_percent_as_number": 48.86363636363637 // 活动分片的百分比
   }
   ```

   ![1566047295115](C:\Users\zxw\Desktop\个人项目笔记\Elasticsearch\1566047295115.png)

2. cluster state:对整个集群的信息进行一个全面的了解，包括集群信息、集群中每个节点的信息、元数据、路由表等

3. cluster pending tasks:返回一个正在添加到更新集群状态的任务列表。集群中的变化通常是很快的，通常这个操作会返回一个空的列表

   ```
   GET /_cluster/pending_tasks
   ```

4. cluster reroute:执行集群重新路由分配命令。例如：把一个分片从一个节点移动到另一个节点，把未分配的分片移动到一个指定的节点

5. cluster update settings:更新集群中的配置，如果是永久配置，就需要重启集群；如果是瞬时配置，就不需要重启集群

   ```
   put /_cluster/settings
   ```

6. Nodes stats:可以获取集群中一个或多个节点的统计信息。

   ```
   get /_nodes/stats
   get /_nodes/nodeId1,nodeId2/stats
   ```

7. Nodes Info:获取集群中一个或多个节点的信息，包括设置、操作系统、虚拟机、线程池等信息

   ```
   get /_nodes
   // 也可以添加参数(settings、os、process、jvm、thread_pool、transport、http、plugins、ingest和indices)返回指定信息。
   get/_nodes/os,jvm
   ```

8. Task Management API:可用于获取Elasticsearch集群中一个或多个节点正在执行中的任务信息

   ```
   get  /_tasks
   ```

9. cluster allocation explain api:解释分片没有被分配的原因

   ```
   get /_cluster_allocation/explian
   ```

### 5.6 监控插件