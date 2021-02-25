加速器2
https://a64gnqd2.mirror.aliyuncs.com


https://gofrp.org/docs/examples/stcp/


需要持久化 frpc.ini


# TODO 
## 
让作业节点自行拉取代码， 需要agent 支持
注意：应该可以使用frp locations 功能，这样少配置一个端口
git clone depth 优化代码下载大小

## 
使用嵌入式数据库


构建主机必须是用加速器，如阿里或者daocloud的加速器


docker run -d --name docker-agent \
    -v /var/run/docker.sock:/var/run/docker.sock \
    -e hostname=qylc-test-2 -e server_addr=39.105.74.223 -e server_port=7700 \
    registry.cn-hangzhou.aliyuncs.com/chainscloud/docker-agent
    
    
# 代码
代码放大gitlab上了


# TODO 
端口冲突时，没有提示