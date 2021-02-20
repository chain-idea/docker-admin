import React from 'react';
import http from "../../../utils/request";
import {Button, Divider, Space, Switch, Table} from "antd";
import {history} from "umi";


export default class extends React.Component {

  state = {
    list: []
  }

  componentDidMount() {
    const {id} = this.props.project;

    http.get("/api/project/apps?id=" + id).then(list => {
      this.setState({list})
    })

  }

  columns = [
    {
      title: '应用名称', dataIndex: 'name', render: (name, row) => {
        return <a onClick={() => history.push("/admin/app/" + row.id)}>{name}</a>
      }
    },


    {title: '最近更新', dataIndex: 'modifyTime'},
    {title: '状态', dataIndex: 'status'},
    {
      title: '自动发布', dataIndex: 'autoDeploy', render: (v, row) => {
        return <Switch checked={v}
                       onChange={checked => {
                         row.autoDeploy = checked
                         this.setState({list: this.state.list})
                         this.setAutoDeploy(row.id, checked)
                       }}
        ></Switch>
      }
    },

  ]

  setAutoDeploy = (id, autoDeploy) => {
    http.get("/api/app/autoDeploy", {id, autoDeploy})
  }


  render() {

    const {list} = this.state;
    const {project} = this.props;

    return (<div>
      <Button className="q-mb-md" onClick={() => history.push("/admin/app/deploy?url=" + project.imageUrl)} type="primary">部署新应用</Button>
      <Table rowKey="id" columns={this.columns} dataSource={list}>

      </Table>
    </div>)
  }


}



