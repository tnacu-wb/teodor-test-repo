import { LiveAssistScriptEmbed } from '@whitbread-eos/atoms';
import { useUserData } from '@whitbread-eos/utils';
import { useEffect } from 'react';

export default function LiveAssist() {
  const { isLoggedIn } = useUserData();
  useEffect(() => {
    isLoggedIn ? showLiveAssist() : hideLiveAssist();
  }, [isLoggedIn]);

  const liveAssistVisibility = (visibility: string) => {
    if (typeof window !== 'undefined') {
      const liveassistContainer = Array.from(document.getElementsByClassName('LPMcontainer'));
      setVisibility(liveassistContainer[0] as HTMLElement, visibility);

      const liveassistExpanded = document.getElementById('lpChat') as HTMLElement;
      setVisibility(liveassistExpanded, visibility);
    }
  };

  const setVisibility = (element: HTMLElement, visibility: string) => {
    element && (element.style.visibility = visibility);
  };
  const hideLiveAssist = () => {
    liveAssistVisibility('hidden');
  };

  const showLiveAssist = () => {
    liveAssistVisibility('visible');
  };

  return <>{isLoggedIn && <LiveAssistScriptEmbed />}</>;
}
