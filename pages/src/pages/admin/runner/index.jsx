import {PlusOutlined} from '@ant-design/icons';
import {Menu, Button, Divider, Dropdown, Modal, Popconfirm, Select} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "@/utils/request";
import {serverUrl} from "@/config";
import {history} from 'umi';
import RemoteSelect from "../../../components/RemoteSelect";

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
    {
      title: 'git地址替换（源）',
      dataIndex: 'gitUrlReplaceSource',
    },

    {
      title: 'git地址替换（目标）',
      dataIndex: 'gitUrlReplaceTarget',
    },

    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        let menu = <div>
          <a key="1" onClick={() => {
            this.state.showEditForm = true;
            this.state.formValues = record;
            this.state.formValues.hostId = record.host.id
            this.setState(this.state)
          }}>修改</a>
          <Divider type="vertical"></Divider>
          <Popconfirm title={'是否确定' + deleteTitle} onConfirm={() => this.handleDelete([record])}>
            <a>删除</a>
          </Popconfirm>
        </div>;


        return menu

      },
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

  handleUpdate = value => {
    value.host = {id: value.hostId}
    let params = {...this.state.formValues, ...value};
    http.post(api + 'update', params).then(rs => {
      this.state.showEditForm = false;
      this.setState(this.state)
      this.actionRef.current.reload();
    })
    ;
  }

  handleDelete = rows => {
    if (!rows) return true;

    let ids = rows.map(row => row.id);
    http.post(api + 'delete', ids, '删除数据').then(rs => {
      this.actionRef.current.reload();
    })
  }

  render() {
    let {showAddForm, showEditForm} = this.state

    return (<div>
      <div className="panel">
        <ProTable
          actionRef={this.actionRef}
          toolBarRender={(action, {selectedRows}) => [
            <Button type="primary" onClick={() => {
              this.state.showAddForm = true;
              this.setState(this.state)
            }}>
              <PlusOutlined/> 新建
            </Button>,
          ]}
          request={(params, sort) => http.getPageableData(api + 'list', params, sort)}
          columns={this.columns}
          rowSelection={false}
          search={false}
          rowKey="id"
        />
      </div>
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


      <Modal
        maskClosable={false}
        destroyOnClose
        title={editTitle}
        visible={showEditForm}
        onCancel={() => {
          this.state.showEditForm = false;
          this.setState(this.state)
        }}
        footer={null}
      >
        <ProTable
          onSubmit={this.handleUpdate}
          form={{initialValues: this.state.formValues}}
          type="form"
          columns={this.columns}
          rowSelection={false}
        />
      </Modal>

    </div>)
  }


}



