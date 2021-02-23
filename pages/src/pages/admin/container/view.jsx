import React from 'react';
import http from "@/utils/request";
import {LazyLog, ScrollFollow} from "react-lazylog";
import {serverUrl} from "../../../config";

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
        <ScrollFollow
          startFollowing={true}
          render={({follow, onScroll}) => (
            <LazyLog url={serverUrl + api + "logByHost?hostId=" + hostId + "&containerId=" + containerId}
                     enableSearch
                     stream follow={follow} onScroll={onScroll}/>
          )}
        />
      </div>
    </div>)
  }


}



