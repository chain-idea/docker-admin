import {PlusOutlined} from '@ant-design/icons';
import {Button, Divider, Dropdown, Menu, Modal, Popconfirm, Tabs} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "../../../utils/request";
import {history} from 'umi';
import common from "../../../utils/common";

const addTitle = "添加主机"
let api = '/api/host/';


export default class extends React.Component {

  state = {
    showAddForm: false,
    showEditForm: false,
    formValues: {},
    cmd: '',
    classifyList: [],
    showClassifyForm: false,
    classifyFormValues: {}
  }
  actionRef = React.createRef();

  columns = [
    {
      title: '主机名称',
      dataIndex: 'name',
      rules: [
        {
          required: true,
          message: '主机名称为必填项',
        },
      ],
      render(name, row) {
        return <a onClick={() => history.push("host/" + row.id)}>{name}</a>
      }
    },
    {
      title: '备注',
      dataIndex: 'remark'
    },

    {
      title: '操作',
      dataIndex: 'option',
      valueType: 'option',
      render: (_, record) => {
        return <Button  onClick={() => history.push("host/" + record.id)}>查看详情</Button>
      },
    },
  ];


  clickAddBtn = () => {
    http.get(api + "getScript").then(rs => {
      this.setState({cmd: rs.data})
    })
    this.state.showAddForm = true;
    this.setState(this.state)
  }
  handleDelete = rows => {
    if (!rows) return true;

    let ids = rows.map(row => row.id);
    http.post(api + 'delete', ids, '删除数据').then(rs => {
      this.actionRef.current.reload();
    })
  }
  componentDidMount() {
    http.get("api/classify/all").then(classifyList => {
      this.setState({classifyList : classifyList})
    })
  }

  render() {
    let {showAddForm} = this.state

    return (<div className="panel">
      <Tabs defaultActiveKey="0">
        {this.state.classifyList.map((classify, index) => <Tabs.TabPane tab={classify.groupName} key={index}>
          <ProTable
            actionRef={this.actionRef}
            toolBarRender={(action, {selectedRows}) => [
              <Button type="primary" onClick={this.clickAddBtn}>
                <PlusOutlined/> 添加主机
              </Button>,
            ]}
            request={(params, sort) => http.getPageableData(api + 'list?classifyId=' + classify.id, params, sort)}
            columns={this.columns}
            rowSelection={false}
            rowKey="id"
            bordered={true}
            search={false}
          />
        </Tabs.TabPane>)}

      </Tabs>


      <Modal
        maskClosable={false}
        destroyOnClose
        title={addTitle}
        width={800}
        visible={showAddForm}
        onCancel={() => {
          this.state.showAddForm = false;
          this.setState(this.state)
        }}
        footer={null}
      >
        <p> 安装主机监控程序</p>
        <p> 安装好 Docker 后，运行主机安装命令。</p>

        <code>
          {this.state.cmd}
        </code>
      </Modal>

    </div>)
  }


}



