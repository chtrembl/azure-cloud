const CAMERA_FOV = 4.0;

const CameraPosition = {
  CENTER: 0.5,
  RIGHT: 0.6666666667,
  LEFT: 0.3333333333,
};

const linearInterpolate = (min, max, percentage) => (1.0 - percentage) * min + percentage * max;

/**
 * Calculate the camera orbit/pan for a given video size and horizontal offset
 *
 * @param {number} videoWidth element width
 * @param {number} videoHeight element height
 * @param {number} percentage horizontal offset, 0 is hard left and 1 is hard right
 */
const calculateCameraPosition = (videoWidth, videoHeight, percentage, tiltDeg = 0) => {
  const ratio = videoWidth / videoHeight;
  const angleRange = ratio * CAMERA_FOV;

  return {
    tiltDeg,
    orbitDegX: linearInterpolate(-20, 20, percentage),
    orbitDegY: 0,
    panDeg: linearInterpolate(-angleRange, angleRange, percentage),
  };
};

export {
  calculateCameraPosition,
  CameraPosition,
};
