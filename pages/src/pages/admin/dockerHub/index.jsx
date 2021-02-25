import {Alert, Button, Space, Typography, message} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "@/utils/request";
import {Link} from 'umi';
import {history} from "../../../.umi/core/history";

let api = '/api/dockerHub/';


export default class extends React.Component {

  columns = [
    {
      title: '名称', dataIndex: 'name', render: (name, row) => {
        let url = 'https://hub.docker.com/_/' + name
        return <a href={url} target="_blank">{name} &nbsp;
          {row.official && <Typography.Text type={"success"}>官方认证</Typography.Text>}</a>

      }
    },
    {title: '描述', dataIndex: 'description'},

    {
      title: '官方收藏数',
      dataIndex: 'starCount',
    },

    {
      title: '-',
      render: (_, row) => {
        const name = row.name;
        return <Space>
          <Button onClick={()=>this.star(name)}>收藏</Button>
          <Button onClick={() => history.push("app/deploy?url=" + row.name)}>部署应用</Button>
        </Space>
      }
    },
  ];
  star(name){
    http.get('api/starImage/star/' + name)
  }

  render() {

    return (<div className="panel">
      <Alert message="请输入关键字搜索 Docker Hub 镜像"></Alert>
      <ProTable
        request={(params, sort) => http.getPageableData(api + 'list', params, sort)}
        columns={this.columns}
        rowSelection={false}
        search={false}
        options={{search: true}}
        rowKey="name"
        pagination={false}
      />


    </div>)
  }


}



