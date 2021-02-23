import React from 'react';
import {LazyLog, ScrollFollow} from 'react-lazylog';
import {serverUrl} from "../../../config";

let api = '/api/container/';


export default class extends React.Component {


  render() {
    const {id} = this.props;
    const {container} = this.props
    let containerId = container.id;
    let url = serverUrl + api + "log?appId=" + id + "&containerId=" + containerId;

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



