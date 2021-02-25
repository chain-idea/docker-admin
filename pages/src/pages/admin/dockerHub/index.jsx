import {PlusOutlined} from '@ant-design/icons';
import {Menu, Button, Divider, Dropdown, Modal, Popconfirm, Select, Alert, Space, Typography} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "@/utils/request";
import RemoteSelect from "../../../components/RemoteSelect";
import {Link} from 'umi';

let api = '/api/dockerHub/';


export default class extends React.Component {

  columns = [
    {
      title: '名称', dataIndex: 'name', render: (name, row) => {
        return <Link>{ name } &nbsp;
          {  row.is_official && <Typography.Text type={"success"}>官方认证</Typography.Text>}</Link>

      }
    },
    {title: '描述', dataIndex: 'description'},

    {
      title: '收藏数',
      dataIndex: 'star_count',
    },
  ];
  handleSave = value => {
    value.host = {id: value.hostId}
    http.post(api + 'save', value).then(rs => {
      this.state.showAddForm = false;
      this.setState(this.state)
      this.actionRef.current.reload();
    })
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
        rowKey="id"
        pagination={false}
      />


    </div>)
  }


}



