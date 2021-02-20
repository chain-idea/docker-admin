import {Select, Form, Button, Card, Col, Divider, Modal, Radio, Row, Space, Tabs, Input} from 'antd';
import React from 'react';
import http from "../../../utils/request";
import Build from "./Build";
import PipelineJnl from "./PipelineJnl";
import Apps from "./Apps";

let api = '/api/project/';


export default class extends React.Component {

  state = {
    project: {},
    showTrigger: false,
    triggerValueList: [],
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
        this.setState({showTrigger: false})
        this.pipelineJnlRef.current.reload();
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
        {project.id && <Tabs defaultActiveKey="1">
          <Tabs.TabPane tab="执行记录" key="1">
            <PipelineJnl ref={this.pipelineJnlRef} project={project}></PipelineJnl>
          </Tabs.TabPane>
          <Tabs.TabPane tab="构建" key="2">
            <Build project={project}></Build>
          </Tabs.TabPane>
          <Tabs.TabPane tab="应用" key="3">
            <Apps project={project}></Apps>
          </Tabs.TabPane>
        </Tabs>
        }
      </div>


      <Modal visible={showTrigger} title="手动触发流水线"
             onOk={this.submitTrigger}
             destroyOnClose={true}
             onCancel={() => this.setState({showTrigger: false})}>
        <Form labelCol={{span: 4}} ref={this.triggerFormRef} initialValues={{type: 'branch', value: 'master'}}
              preserve={false}>
          <Form.Item name="type" label="触发方式">
            <Select>
              <Select.Option value="branch">分支</Select.Option>
              <Select.Option value="tag">标签</Select.Option>
              <Select.Option value="commit">commit </Select.Option>
            </Select>

          </Form.Item>
          <Form.Item name="value" label="值">
            <Input/>
          </Form.Item>
        </Form>
      </Modal>

    </div>)
  }


}



