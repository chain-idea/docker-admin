import {PlusOutlined} from '@ant-design/icons';
import {Menu, Button, Divider, Dropdown, Modal, Popconfirm, Select,Tabs} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "../../../utils/request";
import {serverUrl} from "../../../config";
import {history} from 'umi';
import RemoteSelect from "../../../components/RemoteSelect";
import common from "../../../utils/common";

const addTitle = "添加项目"
const editTitle = '编辑项目'
const deleteTitle = '删除项目'
let api = '/api/project/';
let prefix = serverUrl;
if (prefix.endsWith("/")) {
  prefix = prefix.substr(0, prefix.length - 1);
}


export default class extends React.Component {

  state = {
    showAddForm: false,
    showEditForm: false,
    formValues: {},
    classifyList: [],
  }
  actionRef = React.createRef();

  columns = [
    {
      title: '项目名称',
      dataIndex: 'name',
      sorter: 1,
      rules: [
        {
          required: true,
          message: '项目名称为必填项',
        },
      ],
      render: (name, row) => {
        return <a onClick={() => history.push("project/" + row.id)}>{name}</a>
      }
    },
    {
      title: '备注',
      dataIndex: 'remark',
    },
    {
      title: '最近更新',
      dataIndex: 'modifyTime',
      sorter: true,
      hideInSearch: true,
      hideInForm: true,
    },
    {
      title: 'git仓库',
      dataIndex: 'gitUrl',
      sorter: true,
      rules: [
        {
          required: true,
        },
      ],
    },
    {
      title: 'git帐号',
      dataIndex: 'gitUsername',
      hideInTable: true,
      rules: [
        {
          required: true,
        },
      ],
    },
    {
      title: 'git密码',
      dataIndex: 'gitPassword',
      hideInTable: true,
      rules: [
        {
          required: true,
        },
      ],
    },
    {
      title: '镜像仓库',
      dataIndex: 'registryId',
      hideInTable: true,
      render: v => {
        return v;
      },
      renderFormItem(item, prop) {
        return <RemoteSelect url="/api/registry/options"></RemoteSelect>;
      }

    },
    {
      title: '所属分组',
      dataIndex: 'classifyId',
      hideInTable: true,
      render: v => {
        return v;
      },
      renderFormItem(item, prop) {
        return <RemoteSelect url="/api/classify/options"></RemoteSelect>;
      }

    },
    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, row) => {
        let menu = <Menu>
          <Menu.Item key="1" onClick={() => {
            row.registryId = row.registry.id
            row.classifyId = row.classify? row.classify.id : ""

            this.state.showEditForm = true;
            this.state.formValues = row;
            this.setState({
              showEditForm: true,
              formValues: row
            })
          }}>修改</Menu.Item>
        </Menu>;
        return <Dropdown.Button overlay={menu}
                                onClick={() => history.push("project/" + row.id)}>查看详情</Dropdown.Button>

      },
    },
  ];
  handleSave = value => {
    value.registry = {
      id: value.registryId
    }

    http.post(api + 'save', value).then(rs => {
      this.state.showAddForm = false;
      this.setState(this.state)
      this.actionRef.current.reload();

      // 跳转到项目
      history.push("/admin/project/" + rs.id)
    })
  }

  handleUpdate = value => {
    let params = {...this.state.formValues, ...value};
    this.state.classifyList.map(clssify => {
      if(clssify.id === params.classifyId){
        params.classify = clssify
      }
    })
    http.post(api + 'update', params).then(rs => {
      this.state.showEditForm = false;
      this.setState(this.state)
      this.actionRef.current.reload();
    })
  }

  componentDidMount() {
    http.get("api/classify/all").then(classifyList => {
      //新增加未分组菜单
      const classify = {
        id: "",
        name: "未分组"
      }
      classifyList.unshift(classify)
      this.setState({classifyList : classifyList})
    })
  }

  render() {
    let {showAddForm, showEditForm} = this.state

    return (<div className="panel">
        <Tabs defaultActiveKey="0">
          {this.state.classifyList.length > 0 && this.state.classifyList.map((classify, index) => <Tabs.TabPane tab={classify.name} key={index}>
            <ProTable
              actionRef={this.actionRef}
              search={false}
              toolBarRender={(action, {selectedRows}) => [
                <Button type="primary" onClick={() => {
                  this.state.showAddForm = true;
                  this.setState(this.state)
                }}>
                  <PlusOutlined/> 新建
                </Button>,
              ]}
              request={(params, sort) => http.getPageableData(api + 'list?classifyId=' + classify.id, params, sort)}
              columns={this.columns}
              rowSelection={false}
              rowKey="id"
              bordered={true}
              options={{search: true}}

              size="small"
            />
          </Tabs.TabPane>)}

        </Tabs>


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
          {...common.getTableFormProps()}
          onSubmit={this.handleSave}
          columns={this.columns}

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
          {...common.getTableFormProps(this.state.formValues)}
          onSubmit={this.handleUpdate}
          columns={this.columns}
        />
      </Modal>
    </div>)
  }


}



