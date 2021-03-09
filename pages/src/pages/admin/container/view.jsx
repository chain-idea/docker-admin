import React from 'react';
import ContainerLog from "./ContainerLog";

let api = '/api/container/';


export default class extends React.Component {

  state = {
    dockerInfo: {}
  }


  hostId = null
  containerId = null

  constructor(props) {
    super(props);
    let {params} = props.match;
    this.hostId = params.hostId
    this.containerId = params.containerId
  }


  render() {
    const {containerId, hostId} = this;

    return (<div style={{height: '100%'}}>


      <div style={{height: '100%'}}>
       <ContainerLog containerId={containerId} hostId={hostId}></ContainerLog>
      </div>
    </div>)
  }


}



