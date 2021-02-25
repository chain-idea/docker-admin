import {Alert, Button, Col, Divider, Form, Input, Modal, Popconfirm, Row, Space, Tabs} from 'antd';
import React from 'react';
import http from "../../../utils/request";
import Build from "./Build";
import PipelineJnl from "./PipelineJnl";
import Apps from "./Apps";

import {history} from "umi";

let api = '/api/project/';


export default class extends React.Component {

  state = {
    project: {},
    showTrigger: false,
    triggerValueList: [],
    activeTab: 'jnl'
  }
  triggerFormRef = React.createRef();

  componentDidMount() {
    let {params} = this.props.match;

    http.get(api + "get", params).then(project => {
      this.setState({project})
    })
  }

  triggerPipeline = () => {
    this.setState({showTrigger: true})
  }

  pipelineJnlRef = React.createRef();

  submitTrigger = () => {
    this.triggerFormRef.current.validateFields().then(values => {
      values.id = this.state.project.id;
      http.get("/api/pipeline/trigger", values).then(rs => {
        this.setState({showTrigger: false, activeTab: 'jnl'})
        if (this.pipelineJnlRef.current) {
          this.pipelineJnlRef.current.reload();
        }

      })
    })
  }
  handleDelete = () => {
    http.post(api + 'delete', this.state.project.id, '删除数据').then(rs => {
      Modal.info({
        title: '提示',
        content: rs.msg,
        okText: '项目列表',
        onOk: () => {
          history.push("/admin/project")
        }
      })
    })
  }

  render() {
    const {project, showTrigger} = this.state;
    return (<div>

      <div className="panel">
        <Row align="middle">
          <Col flex="auto">
            <h1>{project.name}</h1>
            <h5>镜像：{project.imageUrl}<Divider type="vertical"></Divider> 代码源: {project.gitUrl}</h5>
          </Col>
          <Col flex="200px">
            <Button onClick={this.triggerPipeline} type="primary">立即构建</Button>
          </Col>
        </Row>
      </div>
      <div className="panel">
        {project.id && <Tabs defaultActiveKey="jnl" activeKey={this.state.activeTab} onChange={activeTab=>{this.setState({activeTab})}}>
          <Tabs.TabPane tab="执行记录" key="jnl">
            <PipelineJnl ref={this.pipelineJnlRef} project={project}></PipelineJnl>
          </Tabs.TabPane>
          <Tabs.TabPane tab="构建" key="2">
            <Build project={project}></Build>
          </Tabs.TabPane>
          <Tabs.TabPane tab="应用" key="3">
            <Apps project={project}></Apps>
          </Tabs.TabPane>

          <Tabs.TabPane tab="设置" key="4">
            <Space direction={"vertical"}>
              <Alert message="请注意，删除项目将清除项目的所有历史数据以及相关的镜像，且该操作不能被恢复，您确定要删除吗?" type={"warning"}></Alert>


              <Popconfirm title={'是否确定项目'} onConfirm={this.handleDelete}>
                <Button type={"primary"} danger>删除项目</Button>
              </Popconfirm>
            </Space>
          </Tabs.TabPane>
        </Tabs>
        }
      </div>


      <Modal visible={showTrigger} title="手动触发流水线"
             onOk={this.submitTrigger}
             destroyOnClose={true}
             onCancel={() => this.setState({showTrigger: false})}>
        <Form ref={this.triggerFormRef}
              layout="vertical"
              initialValues={{value: 'master'}}
              preserve={false}>
          <Form.Item name="value" label="分支、标签、commit id">
            <Input/>
          </Form.Item>
        </Form>
      </Modal>

    </div>)
  }


}



