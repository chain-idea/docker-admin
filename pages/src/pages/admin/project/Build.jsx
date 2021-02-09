import React from 'react';
import http from "../../../utils/request";
import {Button, Form, Switch, Input} from "antd";

let api = '/api/project/';


export default class  extends React.Component {

  state = {
    project: {},
    build: {}
  }

  constructor(props) {
    super(props);
    const {project} = props;
    this.state.project = project;
  }

  componentDidMount() {
    let projectId = this.state.project.id;
    let params = {projectId: projectId};
    http.get("/api/project/getBuild", params).then(build => {

      this.setState({build})
      this.formRef.current.setFieldsValue(build)
    })
  }


  onFinish = (values) => {
    let projectId = this.state.project.id;

    http.post("/api/project/updateBuild?projectId=" + projectId, values)
  }

  formRef = React.createRef();

  render() {

    return (<div>


      <Form
        ref={this.formRef}
        labelCol={{span: 4}}
        onFinish={this.onFinish}
      >

        <Form.Item name="context" label="构建目录">
          <Input></Input>
        </Form.Item>
        <Form.Item name="dockerfile" label="Dockerfile">
          <Input></Input>
        </Form.Item>

        <Form.Item name="useCache" label="使用缓存" valuePropName="checked">
          <Switch></Switch>
        </Form.Item>

        <Form.Item name="autoBuild" label="自动构建" valuePropName="checked">
          <Switch></Switch>
        </Form.Item>

        <Form.Item wrapperCol={{offset: 4}}>
          <Button type="primary" htmlType="submit">
            确定
          </Button>
        </Form.Item>
      </Form>


    </div>)
  }


}



