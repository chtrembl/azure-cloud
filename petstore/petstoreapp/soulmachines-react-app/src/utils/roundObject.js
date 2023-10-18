// stuff like emotional data has way more decimal places than is useful, round values
const roundObject = (o, multiplier = 10) => {
  const output = {};
  Object.keys(o).forEach((k) => {
    output[k] = Math.floor(o[k] * multiplier) / multiplier;
  });
  return output;
};

export default roundObject;
