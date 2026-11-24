import {Composition} from 'remotion';
import {Shorts} from './Shorts';

export const RemotionRoot = () => (
  <Composition id="Shorts" component={Shorts} durationInFrames={600} fps={30} width={1080} height={1920} />
);
