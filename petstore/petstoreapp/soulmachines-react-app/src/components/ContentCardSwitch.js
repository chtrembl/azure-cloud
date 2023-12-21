import React from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import Options from './ContentCards/Options';
import Markdown from './ContentCards/Markdown';
import Link from './ContentCards/Link';
import Image from './ContentCards/Image';
import Video from './ContentCards/Video';
import ButtonWithImage from './ContentCards/ButtonWithImage';
import { setActiveCards, animateCamera } from '../store/sm/index';
import ImageCarousel from './ContentCards/ImageCarousel';

const returnCardError = (errMsg) => {
  console.error(errMsg);
  return <div className="alert alert-danger" key={Math.random()}>{errMsg}</div>;
};

function ContentCardSwitch({
  activeCards,
  dispatchActiveCards,
  card,
  index,
  inTranscript,
  triggerScrollIntoView,
}) {
  const componentMap = {
    options: {
      element: Options,
      removeOnClick: true,
    },
    markdown: {
      element: Markdown,
      removeOnClick: false,
    },
    externalLink: {
      element: Link,
      removeOnClick: false,
    },
    image: {
      element: Image,
      removeOnClick: false,
    },
    imageCarousel: {
      element: ImageCarousel,
      removeOnClick: false,
    },
    video: {
      element: Video,
      removeOnClick: false,
    },
    buttonWithImage: {
      element: ButtonWithImage,
      removeOnClick: false,
    },
  };

  if ('type' in card === false) {
    return returnCardError(
      'payload missing type key! component key has been depreciated.',
    );
  }
  if (card === undefined) {
    return returnCardError(
      'unknown content card name! did you make a typo in @showCards()?',
    );
  }
  const { data, id, type: componentName } = card;

  if (componentName in componentMap === false) {
    return returnCardError(
      `component ${componentName} not found in componentMap!`,
    );
  }
  const { element: Element, removeOnClick } = componentMap[componentName];

  let removeElem;
  if (index) {
    // for some cards, we want them to be hidden after the user interacts w/ them
    // for others, we don't
    removeElem = (e) => {
      // we need to write our own handler, since this is not an interactive element by default
      if (e.type === 'click' || e.code === 'enter') {
        const newActiveCards = [
          ...activeCards.slice(0, index),
          ...activeCards.slice(index + 1),
        ];
        dispatchActiveCards(newActiveCards);
      }
    };
  } else {
    removeElem = null;
  }
  const elem = (
    // disable no static element interactions bc if removeOnClick is true,
    // elem should have interactive children
    // eslint-disable-next-line jsx-a11y/no-static-element-interactions
    <div
      onClick={removeOnClick ? removeElem : null}
      onKeyPress={removeOnClick ? removeElem : null}
      className="m-2"
      data-sm-content={id}
    >
      {/* elements that are interactive but shouldn't be removed immediately
         can use triggerRemoval to have the card removed */}
      <Element
        data={{ id, ...data }}
        triggerRemoval={removeElem}
        inTranscript={inTranscript}
        transcriptIndex={index}
        triggerScrollIntoView={triggerScrollIntoView}
      />
    </div>
  );
  return elem;
}

ContentCardSwitch.propTypes = {
  activeCards: PropTypes.arrayOf(
    PropTypes.shape({
      type: PropTypes.string,
      // eslint-disable-next-line react/forbid-prop-types
      data: PropTypes.object,
    }),
  ).isRequired,
  dispatchActiveCards: PropTypes.func.isRequired,
  inTranscript: PropTypes.bool,
  // eslint-disable-next-line react/forbid-prop-types
  card: PropTypes.object.isRequired,
  index: PropTypes.number.isRequired,
  triggerScrollIntoView: PropTypes.func,
};

ContentCardSwitch.defaultProps = {
  inTranscript: false,
  triggerScrollIntoView: () => console.warn('triggerScrollIntoView is not passed in as a prop—this content card will not be able to influence transcript scrolling!'),
};

const mapStateToProps = ({ sm }) => ({
  activeCards: sm.activeCards,
  videoWidth: sm.videoWidth,
  videoHeight: sm.videoHeight,
  showTranscript: sm.showTranscript,
});

const mapDispatchToProps = (dispatch) => ({
  dispatchActiveCards: (activeCards) => dispatch(
    setActiveCards({ activeCards, cardsAreStale: true }),
  ),
  dispatchAnimateCamera: (options, duration = 1) => dispatch(animateCamera({ options, duration })),
});
export default connect(mapStateToProps, mapDispatchToProps)(ContentCardSwitch);
