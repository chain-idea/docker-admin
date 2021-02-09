import {Button} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "../../../utils/request";

import {history} from "umi";

let api = '/api/repository/';


export default class extends React.Component {

  state = {
    showAddForm: false,
    showEditForm: false,
    formValues: {},
  }
  actionRef = React.createRef();

  columns = [
    {
      title: '名称',
      dataIndex: 'name',
    },
    {
      title: '镜像',
      dataIndex: 'url',
    },
    {
      title: '类型',
      dataIndex: 'type',
    },
    {
      title: '最近更新',
      dataIndex: 'modifyTime',
    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, row) => {
       return <Button onClick={()=>history.push("app/deploy?url=" + row.url)}>部署最新版</Button>
      },
    },
  ];

  render() {

    return (<div>
      <ProTable
        actionRef={this.actionRef}
        request={(params, sort) => http.getPageableData(api + 'list', params, sort)}
        columns={this.columns}
        rowSelection={false}
        rowKey="name"
        size="small"
        bordered={true}
        search={false}
        options={{search:true}}
      />




    </div>)
  }


}



