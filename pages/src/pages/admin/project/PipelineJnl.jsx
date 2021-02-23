import {Col, Modal, Popconfirm, Row} from 'antd';
import React from 'react';
import ProTable from '@ant-design/pro-table';
import http from "../../../utils/request";
import {LazyLog, ScrollFollow} from "react-lazylog";
import {CheckCircleFilled, ClockCircleOutlined, CloseCircleFilled, Loading3QuartersOutlined} from "@ant-design/icons";

let api = '/api/pipeline/';


function getIcon(key, index) {
  const iconDict = {
    PENDING: <ClockCircleOutlined key={index}/>,
    PROCESSING: <Loading3QuartersOutlined key={index} spin/>,
    SUCCESS: <CheckCircleFilled key={index} style={{color: 'green'}}/>,
    ERROR: <CloseCircleFilled key={index} style={{color: 'red'}}/>
  }
  return iconDict[key]
}


export default class extends React.Component {

  constructor(props) {
    super(props);
    this.listURL = api + "list?projectId=" + props.project.id
  }

  listURL = null
  state = {
    showPipelineLog: false,
    curRow: {}
  }
  actionRef = React.createRef();

  reload = () => {
    this.actionRef.current.reload()
  }

  columns = [
    {
      title: 'commit',
      dataIndex: 'commit',
    },
    {
      title: 'commit message',
      dataIndex: 'commitMessage',
    },

    {
      title: '执行状态',
      dataIndex: 'status',
      render: (v, row) => {
        return <Row onClick={() => this.showPipelineLog(row)} gutter={8}>
          {row.stageList.map((stage, sIndex) => <Col key={sIndex}>
              {stage.pipeList.map((pipe, index) => {
                let el = getIcon(pipe.status, index);
                return el;
              })}
            </Col>
          )}
        </Row>
      }
    }, {
      title: '开始时间',
      dataIndex: 'createTime'
    },

    {
      title: '耗时（秒）',
      dataIndex: 'consumeTime',
    },
    {
      title: '操作',
      dataIndex: 'option',
      render: (_, row) => {
        return <Popconfirm title={'是否确定删记录'} onConfirm={() => this.handleDelete([row])}>
          <a>删除</a>
        </Popconfirm>
      }
    }
  ]
  showPipelineLog = (row) => {
    this.setState({showPipelineLog: true, curRow: row})
  }

  handleDelete(rows) {
    if (!rows) return true;

    let ids = rows.map(row => row.id);
    http.post(api + 'delete', ids, '删除数据').then(rs => {
      this.actionRef.current.reload();
    })
  }

  render() {

    const {curRow} = this.state

    return (<div>

      <ProTable
        search={false}
        actionRef={this.actionRef}
        request={(params, sort) => http.getPageableData(this.listURL, params, sort)}
        columns={this.columns}
        rowSelection={false}
        toolBarRender={false}

        rowKey="id"
      />


      <Modal
        width={1200}
        maskClosable={false}
        destroyOnClose
        title="流水线的日志"
        visible={this.state.showPipelineLog}
        onCancel={() => {
          this.setState({showPipelineLog: false})
        }}
        footer={null}
      >

        <div style={{minHeight: 500}}>

          <ScrollFollow
            startFollowing={true}
            render={({follow, onScroll}) => (
              <LazyLog url={curRow.logUrl}
                       websocket
                       stream follow={follow} onScroll={onScroll}/>
            )}
          />

        </div>
      </Modal>


    </div>)
  }


}



