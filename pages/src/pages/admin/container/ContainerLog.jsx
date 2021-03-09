import React from 'react';
import {LazyLog, ScrollFollow} from 'react-lazylog';
import {serverUrl} from "../../../config";

let api = '/api/container/';


export default class ContainerLog extends React.Component {


  render() {
    const {hostId, containerId} = this.props;
    let url = serverUrl + api + "log/" + hostId + "/" + containerId;

    return <div style={{height: 500, width: '100%'}}>
      <ScrollFollow
        startFollowing={true}
        render={({follow, onScroll}) => {

          return (
            <LazyLog url={url}
                     enableSearch
                     stream follow={follow} onScroll={onScroll}/>
          );
        }}
      />
    </div>
  }


}



