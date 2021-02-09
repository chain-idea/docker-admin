import {PlusOutlined} from '@ant-design/icons';
import {Button, Divider, Dropdown, Menu, Modal, Popconfirm, Popover} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "../../../utils/request";
import {history} from 'umi';

const addTitle = "添加应用"
const editTitle = '编辑应用'
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
      render(v){
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

    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        let menu = <Menu>
          <Menu.Item key="1" onClick={() => {
            this.state.showEditForm = true;
            this.state.formValues = record;
            this.setState(this.state)
          }}>修改</Menu.Item>
          <Menu.Item key="2">
            <Popconfirm title={'是否确定' + deleteTitle} onConfirm={() => this.handleDelete([record])}>
              <a>删除</a>
            </Popconfirm>
          </Menu.Item>
        </Menu>;


        return <Dropdown.Button overlay={menu}
                                onClick={() => history.push("app/" + record.id)}>查看详情</Dropdown.Button>

      },
    },
  ];
  handleSave = value => {


    http.post(api + 'save', value).then(rs => {
      this.state.showAddForm = false;
      this.setState(this.state)
      this.actionRef.current.reload();
    })
  }

  handleUpdate = value => {
    let params = {...this.state.formValues, ...value};
    http.post(api + 'update', params).then(rs => {
      this.state.showEditForm = false;
      this.setState(this.state)
      this.actionRef.current.reload();
    })
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

      {/*表格*/}
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


      {/*添加表单*/}
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
          form={{labelCol: {span: 5}, layout: 'horizontal'}}
          columns={this.columns}
          rowSelection={{}}
        />
      </Modal>


      {/*修改表单*/}
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
          rowSelection={{}}
        />
      </Modal>

    </div>)
  }


}



