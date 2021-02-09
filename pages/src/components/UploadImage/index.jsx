import {Icon, Modal, Upload, message} from 'antd';

import {serverUrl, fileUrl} from "../../config";
import {Component} from "react";
import ImgCrop from "antd-img-crop";
import PlusCircleOutlined from "@ant-design/icons";


class UploadImage extends Component {

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
        if (f.startsWith('http')) {
          file.url = f;
        } else {
          file.url = fileUrl + f;
        }
        file.uid = index;
        file.name = 'image.png';
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

  // beforeUpload = (file) => {
  //   const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png';
  //   if (!isJpgOrPng) {
  //     message.error('只能上传JPG/PNG文件!');
  //   }
  //   const isLt2M = file.size / 1024 / 1024 < 0.1;
  //   if (!isLt2M) {
  //     message.error('图片最大10MB!');
  //   }
  //   return isJpgOrPng && isLt2M;
  // }

  handleChange = (e) => {
    const {fileList} = e;
    this.setState({fileList});
    if (this.props.onChange) {
      let file = [];
      fileList.forEach(f => {
        if (f.status === 'done' && f.url) {
          // file.push(f.url)
          file.push(f.fileName)
        }
        if (f.status === 'done' && f.response && f.response.code == 0) {
          // file.push(f.response.url)
          file.push(f.response.fileName)
        }
      })
      let files = file.join(",");
      console.log('uploadResult',files)
      this.props.onChange(files)
    }
  }

  render() {
    const {previewVisible, previewImage, fileList} = this.state;
    const {disabled} = this.props;
    const uploadButton = (
      <div>
        <PlusCircleOutlined />
        <div className="ant-upload-text">添加</div>
      </div>
    );
    const maxNum = this.props.maxNum ? this.props.maxNum : 8;
    let multiple = this.props.multiple ? this.props.multiple : true;
    if (maxNum == 1) {
      multiple = false;
    }
    if (this.props.crop) {
      return (
        <div className="clearfix">
          <ImgCrop {...this.props}>
            <Upload
              action={serverUrl + 'common/upload'}
              listType="picture-card"
              fileList={fileList}
              onPreview={this.handlePreview}
              onChange={this.handleChange}
              headers={{'Authorization': localStorage.getItem("jwt")}}
            >
              {fileList.length >= maxNum ? null : uploadButton}
            </Upload>
          </ImgCrop>
          <Modal visible={previewVisible} footer={null} onCancel={this.handleCancel}>
            <img alt="example" style={{width: '100%'}} src={previewImage}/>
          </Modal>
        </div>
      )
    } else {
      return (
        <div className="clearfix">
          <Upload
            action={serverUrl + 'common/upload'}
            accept='image/*'
            listType="picture-card"
            fileList={fileList}
            onPreview={this.handlePreview}
            onChange={this.handleChange}
            multiple={multiple}
            headers={{'Authorization': localStorage.getItem("jwt")}}
          >
            {fileList.length >= maxNum ? null : uploadButton}
          </Upload>
          <Modal visible={previewVisible} footer={null} onCancel={this.handleCancel}>
            <img alt="example" style={{width: '100%'}} src={previewImage}/>
          </Modal>
        </div>
      )
    }
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

export default UploadImage
