import { extendTheme } from '@chakra-ui/react';

import Alert from './components/Alert';
import Badge from './components/Badge';
import Breadcrumb from './components/Breadcrumb';
import Button from './components/Button';
import Checkbox from './components/Checkbox';
import Datepicker from './components/Datepicker';
import Drawer from './components/Drawer';
import Menu from './components/Menu';
import Radio from './components/Radio';
import Switch from './components/Switch';
import Tabs from './components/Tabs';
import Tooltip from './components/Tooltip';
import { breakpoints } from './foundations/breakpoints';
import { colors } from './foundations/colors';
import { space } from './foundations/space';
import { typography } from './foundations/typography';
import styles from './styles';

const theme = {
  styles,
  colors,
  breakpoints,
  fonts: typography.fonts,
  fontSizes: typography.fontSizes,
  fontWeights: typography.fontWeights,
  lineHeights: typography.lineHeights,
  textStyles: typography.textStyles,
  space,
  components: {
    Tabs,
    Button,
    Menu,
    Alert,
    Tooltip,
    Switch,
    Datepicker,
    Drawer,
    Radio,
    Badge,
    Breadcrumb,
    Checkbox,
  },
};

export default extendTheme(theme);
