import {Upload, Button} from 'antd';


import React, {Component} from "react";
import UploadOutlined from "@ant-design/icons/lib/icons/UploadOutlined";
import {serverUrl} from "../../config";


/**
 * 文件上传组件
 */
class UploadFile extends Component {

  constructor(props) {
    super(props);
  }

  state = {
    previewVisible: false,
    previewImage: '',
    fileList: [],
  };

  componentDidMount() {
    // 初始值
    if (this.props.value && this.props.value.length > 0) {
      let list = [];
      let value = this.props.value;
      if (value instanceof Array) {
      } else {
        value = value.split(',');
      }
      value.forEach((f, index) => {
        let file = {};
        file.url = f;
        file.uid = index;
        file.name = f.split('/')[f.split('/').length - 1];
        file.status = 'done';
        file.fileName = f;
        list.push(file);
      })
      this.setState({fileList: list});
    }
  }

  handleCancel = () => this.setState({previewVisible: false});

  handlePreview = async file => {
    if (!file.url && !file.preview) {
      file.preview = await getBase64(file.originFileObj);
    }

    this.setState({
      previewImage: file.url || file.preview,
      previewVisible: true,
    });
  };

  handleChange = ({fileList}) => {
    this.setState({fileList});
    if (this.props.onChange) {
      let file = [];
      fileList.forEach(f => {
        if (f.status === 'done' && f.url) {
          file.push(f.fileName)
        }
        if (f.status === 'done' && f.response && f.response.code == 0) {
          file.push(f.response.fileName)
        }
      })
      this.props.onChange(file.join(","))
    }
  }

  render() {
    const {fileList} = this.state;
    const uploadButton = (
      <Button>
        <UploadOutlined/> 点击上传
      </Button>
    );
    const maxNum = this.props.maxNum ? this.props.maxNum : 8;
    return (
      <div className="clearfix">
        <Upload
          action={serverUrl + 'common/upload'}
          fileList={fileList}
          listType="text"
          onPreview={this.handlePreview}
          onChange={this.handleChange}
          headers={{'Authorization': localStorage.getItem("jwt")}}>
          {fileList.length >= maxNum ? null : uploadButton}
        </Upload>
      </div>
    )
  }
}

function getBase64(file) {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.readAsDataURL(file);
    reader.onload = () => resolve(reader.result);
    reader.onerror = error => reject(error);
  });
}

export default UploadFile
