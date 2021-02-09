/**
 * 传入的选中的值是对象
 */
import RemoteSelect from "../RemoteSelect";
import React from "react";

class RemoteSelectObject extends React.Component {

  constructor(props) {
    super(props);
  }


  render() {
    return <RemoteSelect {...this.props}
                         onChange={value => {
                           this.props.onChange({id: value})
                         }}
                         value={this.props.value != null ? this.props.value.id : null}></RemoteSelect>
  }

}

export default RemoteSelectObject
