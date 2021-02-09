import {message, TreeSelect} from 'antd';

import React from "react";
import http from "../../utils/request";

/**
 * props : url
 */

export default class RemoteTreeSelect extends React.Component {

  state = {
    data: [],
    value: [],
    fetching: false,
    key: this.props.id,
  };

  componentDidMount() {
    // 下载默认值的label
    this.fetchData();
  }

  componentWillReceiveProps(nextProps){
    if(nextProps.url !== this.props.url){
      this.setState({url: nextProps.url},()=>{
        this.fetchData();
      })
    }
  }

  fetchData = () => {
    const {url} = this.props;
    this.setState({fetching: true});

    http.get(url).then(rs => {
      if (rs == null) {
        console.error(url, '未查询到数据')
        return;
      }
      if (rs.error) {
        message.error(rs.msg);
        this.setState({fetching: false});
        return;
      }


      let list = rs;

      if (!(list instanceof Array)) {
        message.error('返回结果应该为数组');
        this.setState({fetching: false});
        return;
      }
      this.setState({data: list, fetching: false});
    })
  };

  handleChange = value => {
    console.log(value)
    if(this.props.onChange) {
      this.props.onChange(value)
    }
  };

  render() {
    const {data} = this.state;
    console.log(this.props.value, data)
    if (this.state.fetching) {
      return <div>加载中</div>
    }

    return (
      <TreeSelect
        style={{width: '100%'}}
        allowClear={true}
        dropdownStyle={{maxHeight: 400, overflow: 'auto'}}
        treeData={data}
        placeholder="请选择"
        showCheckedStrategy={TreeSelect.SHOW_ALL}
        treeDefaultExpandAll={false}
        onChange={this.handleChange}
        {...this.props}
        value = {this.props.value instanceof Array ? this.props.value : (this.props.value ? [this.props.value] : [])}
      />
    );

  }
}

