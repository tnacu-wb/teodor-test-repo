---
name: semantic-typography-migration
description: "Use when: migrating a specific FE monorepo component or mapped UI text from legacy Chakra typography props to semantic textStyle tokens with the typography feature-toggle helper. Trigger when the user provides a Figma text mapping, semantic typography token, test id, class name, visible text, or component file."
---

# Semantic Typography Migration Skill

Use this skill to migrate one mapped UI text element or component from legacy Chakra typography props to the new semantic `textStyle` tokens, guarded by the typography feature-toggle helper. It can be reused across different components, pages, and apps, but it only owns the component-level migration.

The user will usually provide:
- Figma text/content or visual component reference
- mapped code anchor: component file, test id, class name, visible text, useTranslation label key, or nearby selector
- new semantic typography token, for example `heading-xl` or `body-s-regular`

## Goal

Make the smallest safe migration for the mapped UI text.

The skill must:
- identify the target component or exact UI element from the user's anchor
- confirm the identified target with the user before editing
- migrate only the confirmed component/text element
- use the migration pattern that matches the current legacy styling mechanism

Current final migration direction:
- legacy props like `fontSize`, `fontWeight`, `lineHeight`, `letterSpacing`, `fontFamily`, `textTransform` move to semantic `textStyle`
- layout/visual styles like `m`, `mb`, `color`, `w`, `display`, `textDecoration`, `border`, `gap` stay separate
- temporary FT logic is easy to remove later

## First Response

If the user has not provided all required inputs, ask only for the missing pieces:

1. Which code anchor should be migrated? Ask for one of: file path, component name, test id, class name, visible text, useTranslation label key, or nearby code.
2. Which semantic typography token should be applied?

Do not ask broad questions if the anchor and token are already clear.

## Investigation Flow

Start from the concrete anchor provided by the user.

1. Locate the mapped component or element.
2. Identify the current typography styling mechanism.
3. Find the smallest local migration point.
4. Summarise the identified target and ask the user to confirm before editing.
5. If the typography pattern is unclear or dynamic enough to risk changing behaviour, ask the user before editing.

Common anchors:
- `data-testid`
- class name
- component file
- displayed text
- useTranslation/i18n label key, for example `priceFinder.MVP.customTerms`
- style object name
- Figma-provided component name mapped by the user

## Confirmation Before Editing

Before making file changes, confirm the target with the user unless they explicitly say to proceed without confirmation.

Use a concise confirmation like:

```md
I found the target as `<component/display text>` in `<file path>`.

Current typography pattern: `<style object spread | inline props | sx | style factory | config-driven>`.
Proposed token: `<semantic-token>`.

Should I migrate this element?
```

If multiple possible targets are found, list the likely matches and ask the user to choose one. Do not migrate all matches unless the user explicitly asks for that.

## Current Legacy Typography Patterns

Recognise these current patterns.

### 1. Style Object Spread

```tsx
const textStyle = {
  fontSize: 'md',
  fontWeight: 'semibold',
  lineHeight: '3',
  color: 'darkGrey1',
} as TextProps;

<Text {...textStyle}>Back</Text>
```

### 2. Inline Chakra Typography Props

```tsx
<Text fontSize="lg" fontWeight="semibold" lineHeight="3" color="darkGrey1">
  Price
</Text>
```

### 3. Spread Plus Inline Override

```tsx
<Text {...addressStyle} fontWeight={isChecked ? 'semibold' : 'normal'}>
  Current address
</Text>
```

### 4. Style Factory

```tsx
const allergyTextStyle = (freeBreakfastOption: boolean) => ({
  fontSize: 'sm',
  fontWeight: 'normal',
  lineHeight: '2',
  color: 'btnSecondaryEnabled',
  pt: { mobile: freeBreakfastOption ? '' : 'sm', md: 0 },
});

<Text {...allergyTextStyle(freeBreakfastOption)}>Allergy info</Text>
```

### 5. Composed Style Object

```tsx
const descriptionTextStyle = {
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'normal',
  color: 'darkGrey2',
};

const priceTextStyle = {
  ...descriptionTextStyle,
  fontWeight: 'semibold',
  color: 'darkGrey1',
};
```

### 6. Style Prop Passed To Child

```tsx
<FreePrice priceTextStyle={priceTextStyle} />
```

### 7. `sx` Style Object

```tsx
<Text sx={styles.statusText}>Complete</Text>
```

```ts
statusText: {
  fontWeight: 'semibold',
  color: 'baseBlack',
}
```

### 8. Native `style`

```tsx
<Text style={{ textDecoration: 'underline' }}>Terms</Text>
```

Do not try to make the helper support native `style`. Convert to Chakra props if needed.

### 9. Responsive Chakra Typography Object

```tsx
const termsTextStyle = {
  fontSize: { base: 'xs', md: 'sm' },
  lineHeight: '1.4',
  color: 'gray.600',
};

<Text {...termsTextStyle}>Terms</Text>
```

Chakra supports responsive values for `textStyle`, so this can migrate to a responsive semantic token map when the design mapping needs different semantic styles at different breakpoints.

### 10. Inherited or Theme-Provided Typography

Sometimes the mapped text has no explicit local typography props. For example, text inside a Chakra/atoms `Button` may inherit `fontWeight`, `fontSize`, or `lineHeight` from the button theme, while the local `Text` only carries state colour or layout props.

```tsx
<Button variant="generic" data-testid={formatDataTestId(baseDataTestId, 'SignIn-Option')}>
  <Text {...(isSelected && selectedTextColor)}>Sign in</Text>
</Button>
```

Do not synthesize legacy typography from inherited parent or theme styles. If the component itself does not explicitly set typography, use an empty legacy typography object so the feature-off path preserves the existing inheritance exactly.

## Migration Pattern

Use this explicit split for typography only. Keep the helper simple.

Do not refactor layout props into a new object just for migration. Keep layout usage in its current form (inline props, existing spread object, `sx`, etc.).

```tsx
const getTypographyProps = useSemanticTypography();

<Text
  {...exampleLayoutStyles}
  {...getTypographyProps({}, exampleSemanticTypography)}
>
  Content
</Text>
```

Naming convention:

```ts
const exampleLayoutStyles = {};
const exampleSemanticTypography = { textStyle: 'token-name' };
```

`exampleLayoutStyles` is optional and only applies when a layout style object already exists or is genuinely useful. Do not create it by moving existing layout props out of current usage.

Rules:
- Keep non-typography props where they currently live unless there is an existing layout object already in use.
- Do not move inline/layout props into a new `*LayoutStyles` object purely for migration consistency.
- Put legacy typography props only in `*LegacyTypography`.
- If typography is inherited from a parent component, design system theme, or variant and not explicit on the mapped element, pass `{}` inline as the legacy typography argument.
- Put the semantic token in `*SemanticTypography`.
- Do not let the hook strip or inspect mixed style objects if you can split them clearly.
- Keep existing colours/layout/spacing unchanged unless the token migration explicitly requires otherwise.

### Chakra-themed components with a baseStyle fontSize (e.g. Badge)

The standard pattern above works for Chakra primitives like `Text` and `Box` that have no component-level `baseStyle.fontSize`.

For Chakra-themed components that **do** have a `baseStyle.fontSize` (such as the `Badge` atom), Chakra's CSS-in-JS injects component `__css` after style-prop expansions, meaning neither spreading `{ textStyle: 'token-name' }` inline nor placing it in `sx` can override the built-in `fontSize`.

For these components, resolve the token to its actual CSS properties using `semanticTextStyles` from the adapter, then spread the resolved properties directly:

```tsx
import { semanticTextStyles } from '../../theme/adapters/semanticTypography';

// inside the component:
const typographyProps = getTypographyProps(resolvedLegacy, resolvedSemantic);
const isSemanticMode = 'textStyle' in typographyProps;
const resolvedTypographyStyles = isSemanticMode
  ? (semanticTextStyles[(typographyProps as SemanticTypographyProps).textStyle as string] ?? {})
  : typographyProps;

<ChakraComponent {...otherProps} {...resolvedTypographyStyles} />
```

When `isSemanticMode` is true, the spread becomes `{ fontSize: '12px', fontWeight: 600, … }` — explicit inline style props that reliably override the component's `baseStyle.fontSize`.

The FT guard and legacy/semantic split remain unchanged; only the final spread differs.

Typography props to move out of legacy style objects:

```ts
fontSize
fontWeight
lineHeight
letterSpacing
fontFamily
textTransform
```

Usually keep these outside typography:

```ts
color
m
mt
mb
ml
mr
p
pt
pb
pl
pr
w
h
display
textDecoration
whiteSpace
textAlign
border
background
gap
```

## Helper Hook Contract

Use the existing shared helper if present. If not present and the user asks to implement it, add a small hook in the appropriate shared utils/hooks location following repo export patterns.

Expected helper shape:

~~~ts
import type { TextProps } from '@chakra-ui/react';
import { FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION } from '@whitbread-eos/api';
import { useFeatureToggle } from '@whitbread-eos/utils';

type LegacyTypographyKeys =
  | 'fontSize'
  | 'fontWeight'
  | 'lineHeight'
  | 'letterSpacing'
  | 'fontFamily'
  | 'textTransform';

type LegacyTypographyProps = Pick<TextProps, LegacyTypographyKeys>;
type SemanticTypographyProps = Pick<TextProps, 'textStyle'>;

export function useSemanticTypography() {
  const { [FT_SEMANTIC_TYPOGRAPHY_TOKENIZATION]: isSemanticTypographyEnabled } = useFeatureToggle();

  return (
    legacyTypography: LegacyTypographyProps,
    semanticTypography: SemanticTypographyProps
  ): LegacyTypographyProps | SemanticTypographyProps => {
    return isSemanticTypographyEnabled && semanticTypography.textStyle
      ? semanticTypography
      : legacyTypography;
  };
}
~~~

Important:
- Do not call `useFeatureToggle()` from a plain function.
- The helper must be a React hook or used inside a hook/component.
- Keep the helper intentionally narrow for easier final cleanup.

## Pattern-Specific Migration Examples

### Style Object Spread

Before:

```tsx
<Text {...textStyle}>Back</Text>
```

During migration:

```tsx
const getTypographyProps = useSemanticTypography();

<Text {...textLayoutStyles} {...getTypographyProps(textLegacyTypography, textSemanticTypography)}>
  Back
</Text>
```

After migration:

```tsx
<Text {...textLayoutStyles} textStyle="body-m-regular">
  Back
</Text>
```

### Inline Props

Before:

```tsx
<Text fontSize="lg" fontWeight="semibold" lineHeight="3" color="darkGrey1">
  Price
</Text>
```

During migration:

```tsx
<Text {...priceLayoutStyles} {...getTypographyProps(priceLegacyTypography, priceSemanticTypography)}>
  Price
</Text>
```

### Spread Plus Inline Override

Before:

```tsx
<Text {...addressStyle} fontWeight={isChecked ? 'semibold' : 'normal'}>
  Current address
</Text>
```

During migration:

```tsx
const addressLegacyTypography = {
  ...addressBaseLegacyTypography,
  fontWeight: isChecked ? 'semibold' : 'normal',
};

const addressSemanticTypography = {
  textStyle: isChecked ? 'body-m-emphasis' : 'body-m-regular',
};

<Text {...addressLayoutStyles} {...getTypographyProps(addressLegacyTypography, addressSemanticTypography)}>
  Current address
</Text>
```

Concrete example (preserve current layout usage, split only typography):

```tsx
<Text {...addressStyle} {...getTypographyProps(addressLegacyTypography, addressSemanticTypography)}>
  Current address
</Text>
```

```ts
const addressLegacyTypography = {
  fontSize: 'md',
  lineHeight: '3',
  fontWeight: isChecked ? 'semibold' : 'normal',
};

const addressSemanticTypography = {
  textStyle: isChecked ? 'body-m-emphasis' : 'body-m-regular',
};
```

### Style Factory

Before:

```tsx
<Text {...allergyTextStyle(freeBreakfastOption)}>Allergy info</Text>
```

During migration:

```tsx
<Text
  {...allergyLayoutStyles(freeBreakfastOption)}
  {...getTypographyProps(allergyLegacyTypography, allergySemanticTypography)}
>
  Allergy info
</Text>
```

### `sx`

Before:

```tsx
<Text sx={styles.statusText}>Complete</Text>
```

During migration:

```tsx
<Text
  sx={styles.statusTextLayoutStyles}
  {...getTypographyProps(styles.statusTextLegacyTypography, styles.statusTextSemanticTypography)}
>
  Complete
</Text>
```

Do not build a recursive `sx` transformer unless explicitly requested.

### Responsive `textStyle`

Use responsive `textStyle` only when the semantic mapping differs by breakpoint or the existing legacy typography is responsive.

Before:

```tsx
const termsTextStyle = {
  fontSize: { base: 'xs', md: 'sm' },
  lineHeight: '1.4',
  color: 'gray.600',
  textAlign: 'left',
};

<Text {...termsTextStyle}>Terms</Text>
```

During migration:

```tsx
const getTypographyProps = useSemanticTypography();

const termsTextStyle = {
  color: 'gray.600',
  textAlign: 'left',
};

const termsLegacyTypography = {
  fontSize: { base: 'xs', md: 'sm' },
  lineHeight: '1.4',
};

const termsSemanticTypography = {
  textStyle: { base: 'body-xs-regular', md: 'body-s-regular' },
};

<Text
  {...termsTextStyle}
  {...getTypographyProps(termsLegacyTypography, termsSemanticTypography)}
>
  Terms
</Text>
```

Post migration:

```tsx
<Text {...termsTextStyle} textStyle={{ base: 'body-xs-regular', md: 'body-s-regular' }}>
  Terms
</Text>
```

If one semantic token already includes the intended responsive typography, prefer a single token:

```tsx
<Text {...termsTextStyle} textStyle="body-s-regular">
  Terms
</Text>
```

### Style Passed To Child (Concrete)

Keep ownership where the style is currently owned and pass only selected typography branch.

```tsx
<FreePrice
  priceTextStyle={getTypographyProps(priceLegacyTypography, priceSemanticTypography)}
/>
```

```ts
const priceLegacyTypography = {
  fontSize: 'sm',
  lineHeight: '2',
  fontWeight: 'semibold',
};

const priceSemanticTypography = {
  textStyle: 'body-s-emphasis',
};
```

### Inherited or Theme-Provided Typography

Before:

```tsx
<Button variant="generic" data-testid={formatDataTestId(baseDataTestId, 'SignIn-Option')}>
  <Text {...(isSelected && selectedTextColor)}>Sign in</Text>
</Button>
```

During migration:

```tsx
const getTypographyProps = useSemanticTypography();

const signInOptionSemanticTypography = {
  textStyle: 'label-xl',
};

<Button variant="generic" data-testid={formatDataTestId(baseDataTestId, 'SignIn-Option')}>
  <Text
    {...(isSelected && selectedTextColor)}
    {...getTypographyProps({}, signInOptionSemanticTypography)}
  >
    Sign in
  </Text>
</Button>
```

Only use explicit legacy typography values when they are already present on the mapped element or in a local style object owned by that element.

## Handling Unrecognised Patterns

Pause and ask the user if:
- the mapped UI text is rendered by multiple branches and the target branch is unclear
- the typography comes from CMS/AEM/content config rather than local styles
- the style is generated deep in a form renderer or table renderer and multiple fields would be affected
- the semantic token does not appear to match the existing visual hierarchy
- applying the helper would require large unrelated refactoring
- the migration would require touching unrelated typography in the same file or nearby components

Ask concise questions with options where possible.

## Validation

After editing, run the narrowest useful validation available:
- related component test if obvious
- package/app TypeScript check if scoped command exists
- lint/typecheck for touched package if affordable
- otherwise run `git diff` and report that executable validation was not run

Do not broaden validation to the whole monorepo unless necessary.

## Final Response Format

Keep the final summary short and review-friendly:

```md
Updated the mapped typography in <component/file>.

Changed:
- split legacy typography from layout styles
- added semantic token `<token>` behind `useSemanticTypography()`
- kept the change behind the component-level typography helper

Validation:
- <command/result or not run>

Notes:
- <any follow-up, unclear mapping, or cleanup reminder>
```

If no edits were made, explain what blocked the migration and what input is needed.
