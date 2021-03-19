import {PlusOutlined} from '@ant-design/icons';
import {Button, Divider, Dropdown, Menu, Modal, Popconfirm, Popover, Tabs, Tag} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "../../../utils/request";
import {history} from 'umi';

const deleteTitle = '删除应用'
let api = '/api/app/';


export default class extends React.Component {

  state = {
    classifyList: [],
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
      dataIndex: 'containerStatus',
      render: s => {
        if (s && s.indexOf('Up') >= 0) {
          return <Tag color={"green"}>{s} </Tag>
        }
        return <Tag color={"red"}>{s || '未知'}</Tag>
      }

    },

    {
      title: '最近更新',
      dataIndex: 'modifyTime',

    },

  ];
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

    return (<div className="panel">
      <Tabs defaultActiveKey="0">
        {this.state.classifyList.length > 0 &&this.state.classifyList.map((classify, index) => <Tabs.TabPane tab={classify.name} key={index}>
          <ProTable
            actionRef={this.actionRef}
            toolBarRender={(action, {selectedRows}) => [
              <Button type="primary" onClick={() => {
                history.push("/admin/image")

              }}>
                <PlusOutlined/> 创建应用
              </Button>,
            ]}
            request={(params, sort) => http.getPageableData(api + 'list?classifyId=' + classify.id, params, sort)}
            columns={this.columns}
            rowSelection={false}
            rowKey="id"
            bordered={true}
            search={false}
            options={{search:true}}
          />
        </Tabs.TabPane>)}

      </Tabs>



    </div>)
  }


}



