// this file enables us to pass webRTC/mediaStream data outside of the redux store,
// which we can't do because they're instances of classes, which are not serializeable
import { createAction } from '@reduxjs/toolkit';

const proxyVideo = document.createElement('video');

export default proxyVideo;

// we need to create a copy of the action here, otherwise
// it would create a dependency cycle and a seperate file doesn't seem worth it
const setCameraState = createAction('sm/setCameraState');
// ### handles webcam stream ###
class UserMediaStream {
  constructor() {
    this.userMediaStream = null;
    this.videoOff = false;
    this.dispatch = null;
    this.scene = null;
  }

  // use dispatch to tell redux state what camera state is
  // should be called before setUserMediaStream so we can tell the store the stream's dimensions
  passDispatch = (dispatch) => {
    this.dispatch = dispatch;
  };

  setUserMediaStream = (stream, audioOnly = false) => {
    this.videoOff = !audioOnly;
    // call passDispatch before this so we have access to store
    if (this.dispatch === null) throw new Error('call passDispatch() before setUserMediaStream()!');
    // store stream data so we can access it later
    if (stream !== null) this.userMediaStream = stream;
    // try to get video stream dimensions, if it doesn't work, then we can presume
    // that we don't have access to the webcam.
    try {
      // send webcam stream dimensions to store
      const track = stream.getVideoTracks()[0];
      const { width: cameraWidth, height: cameraHeight } = track.getSettings();
      this.dispatch(setCameraState({ cameraOn: true, cameraWidth, cameraHeight }));
    } catch {
      this.dispatch(setCameraState({ cameraOn: false }));
    }
  };

  getUserMediaStream = () => this.userMediaStream;

  // NOTE: renders emotional recognition nonfunctional, not recommended for use as of 7/14/21
  // if we toggle video, we need to provide scene w/ the new feed
  enableToggle = (scene) => {
    this.scene = scene;
  };

  // NOTE: renders emotional recognition nonfunctional, not recommended for use as of 7/14/21
  toggleVideo = async () => {
    if (this.scene !== null) {
      const { videoOff, userMediaStream } = this;
      const track = userMediaStream.getVideoTracks()[0];

      if (videoOff === false) {
        track.stop();
        this.videoOff = true;
        this.dispatch(setCameraState({ cameraOn: false }));
      } else {
      // we need to re-request the stream from the webcam after it's been stopped
        const newVideoStreamGrab = await navigator.mediaDevices.getUserMedia({
          video: true,
        });
        // delete old track
        this.userMediaStream.removeTrack(track);
        // add new track to media stream
        this.userMediaStream.addTrack(newVideoStreamGrab.getVideoTracks()[0]);
        // ### THIS IS WHERE WE WOULD PROVIDE SCENE W/ THE NEW STREAM ###
        // this.scene.session().userMediaStream = this.userMediaStream;
        this.videoOff = false;
        this.dispatch(setCameraState({ cameraOn: true }));
      }
    }
  };
}

export const mediaStreamProxy = new UserMediaStream();
