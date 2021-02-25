import {Alert, Button, Space, Typography, message} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "@/utils/request";
import {Link} from 'umi';
import {history} from "../../../.umi/core/history";

let api = '/api/starImage/';


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
      title: '赞',
      dataIndex: 'starCount',
    },

    {
      title: '-',
      render: (_, row) => {
        const name = row.name;
        return <Space>
          <Button onClick={()=>this.unstar(name)}>取消收藏</Button>
          <Button onClick={() => history.push("app/deploy?url=" + row.name)}>部署应用</Button>
        </Space>
      }
    },
  ];
  unstar(name){
    http.get('api/starImage/unstar' ,{ name}).then(rs=>{
      this.tableRef.current.reload()
    })
  }

  tableRef = React.createRef();
  render() {

    return (<div className="panel">
      <Alert message={"我收藏的镜像"}></Alert>
      <ProTable
        actionRef={this.tableRef}
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



