import {PlusOutlined} from '@ant-design/icons';
import {Menu, Button, Divider, Dropdown, Modal, Popconfirm, Select, Alert, Space} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "@/utils/request";
import RemoteSelect from "../../../components/RemoteSelect";
import {Link} from 'umi';

const addTitle = "添加模板"
const editTitle = '编辑模板'
const deleteTitle = '删除模板'
let api = '/api/runner/';


export default class extends React.Component {

  state = {
    showAddForm: false,
    showEditForm: false,
    formValues: {},
  }
  actionRef = React.createRef();

  columns = [
    {
      title: '主机',
      dataIndex: 'hostId',
      render: (v, row) => {
        return row.host.name
      },
      renderFormItem: h => {
        return <RemoteSelect url="api/host/options" showSearch></RemoteSelect>
      }

    },
    {
      title: '优先级',
      dataIndex: 'seq',
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
    let {showAddForm, showEditForm} = this.state

    return (<div className="panel">
      <Alert message="请输入关键字搜索 Docker Hub 镜像"></Alert>
      <ProTable
        actionRef={this.actionRef}
        request={(params, sort) => http.getPageableData(api + 'list', params, sort)}
        columns={this.columns}
        rowSelection={false}
        search={false}
        options={{search: true}}
        rowKey="id"
      />
      <Modal
        maskClosable={false}
        destroyOnClose
        title={addTitle}
        visible={showAddForm}
        onCancel={() => {
          this.state.showAddForm = false;
          this.setState(this.state)
        }}
        footer={null}
      >
        <ProTable
          onSubmit={this.handleSave}
          type="form"
          columns={this.columns}
          rowSelection={false}
        />
      </Modal>


    </div>)
  }


}



