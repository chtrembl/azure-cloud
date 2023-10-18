/* eslint-disable import/prefer-default-export */
import smLogo from './img/sm-logo-retina.webp';
import placeholder from './img/placeholder-headshot.png';

// header will not take up vertical height when transparent, so you need to be mindful of overlap
export const transparentHeader = true;
export const headerHeight = '4.2rem';
export const logo = smLogo;
export const logoAltText = 'Soul Machines logo';
export const logoLink = '/';

// background image is positioned in a way that is best for pictures of the persona's face.
// adjust spacing as necessary in Landing.js for different images
// if you want just a color, set landingBackgroundImage to null
// if desired, a gradient can also be added to landingBackgroundColor
export const landingBackgroundColor = '#FFF';
export const landingBackgroundImage = placeholder;

// if set to true, on disconnect, the app will redirect to the specified route.
// if false, it will redirect to /
export const disconnectPage = true;
export const disconnectRoute = '/feedback';
