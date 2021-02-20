#!/bin/sh
set -e

# 安装frpc
# curl -s http://www.gzqylc.com:7002/frp/install_frpc.sh | sh -s

web_api=arg_web_server

echo '脚本开始执行'


# 函数：检查命令行是否存在
command_exists() {
	command -v "$@" > /dev/null 2>&1
}

# 检查操作系统

# 检查docker是否安装
if ! command_exists docker; then
		echo "请先安装docker"
		exit 1
fi

# 获得docker唯一标识
docker_id=$(curl -s -XGET --unix-socket /var/run/docker.sock http://localhost/info | cut -d "\"" -f 4  >&1)
echo "容器引擎标识: ${docker_id}"

id=$(tr [A-Z] [a-z] <<< "${docker_id}" | sed 's/://g' >&1)
echo "最小化标识 ${id}"



echo "下载frpc..."
curl  "${web_api}/frp/frpc" -o /usr/bin/frpc && chmod +x /usr/bin/frpc
curl  "${web_api}/frp/frpc.service" -o /etc/systemd/system/frpc.service
mkdir /etc/frp
curl  "${web_api}/frp/${id}/frpc.ini" -o /etc/frp/frpc.ini
echo "下载frpc完毕"

systemctl daemon-reload
systemctl enable frpc
systemctl start frpc

curl "${web_api}/api/host/notifyAdd/${id}"

echo "安装完毕"