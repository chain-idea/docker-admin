import {PlusOutlined} from '@ant-design/icons';
import {Button, Divider, Dropdown, Menu, Modal, Popconfirm, Popover} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "../../../utils/request";
import {history} from 'umi';

const deleteTitle = '删除应用'
let api = '/api/app/';


export default class extends React.Component {

  state = {
    showAddForm: false,
    showEditForm: false,
    formValues: {},
  }
  actionRef = React.createRef();

  columns = [
    {
      title: '应用名称',
      dataIndex: 'name',
      render: (name, row) => {
        return <a onClick={() => history.push("app/" + row.id)}>{name}</a>
      }
    },
    {
      title: '主机',
      dataIndex: 'host',
      render(v) {
        return v.name
      }
    },
    {
      title: '镜像',
      dataIndex: 'imageUrl',

    },

    {
      title: '版本',
      dataIndex: 'imageTag',

    },
    {
      title: '状态',
      dataIndex: 'imageTag',

    },

    {
      title: '最近更新',
      dataIndex: 'modifyTime',

    },

  ];



  render() {

    return (<div>

      <ProTable
        actionRef={this.actionRef}
        toolBarRender={(action, {selectedRows}) => [
          <Button type="primary" onClick={() => {
            history.push("repository")

          }}>
            <PlusOutlined/> 创建应用
          </Button>,
        ]}
        request={(params, sort) => http.getPageableData(api + 'list', params, sort)}
        columns={this.columns}
        rowSelection={false}
        rowKey="id"
        bordered={true}
        search={false}
      />


    </div>)
  }


}



