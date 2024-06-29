import { Card, CardContent, Grid, GridSize, Typography } from "@mui/material"
import React, { ReactNode } from "react"
import { GridItemData } from "../../utils/types"
import { useTranslation } from "react-i18next"

type GridCardProps = {
  title?: string
  data: GridItemData[]
  labelSize?: GridSize
  contentSize?: GridSize
  noBorder?: boolean
  rowSpacing?: number
}

export default function GridCard({ title, data, labelSize, contentSize, noBorder, rowSpacing } : GridCardProps) {
  const { t } = useTranslation();
  const styleWithBorder = { border: `1px solid black`, marginBottom: 3 }
  const styleWithoutBorder = { marginBottom: 3 }

  return (
    <Card elevation={noBorder ? 0 : 1} sx={ noBorder ? styleWithoutBorder : styleWithBorder }>
      <CardContent>
        {title && <Typography variant="h5" sx={{ marginBottom: '10px' }}>{t(title)}</Typography>} 
        <Grid container spacing={rowSpacing ?? 0.5}>
          {
            data.map((item, index) => (
              <React.Fragment key={index}>
                <Grid item xs={labelSize ?? 3}>
                  <LabelTypography>{t(item.label)}:</LabelTypography>
                </Grid>
                <Grid item xs={contentSize ?? 9}>
                  <ContentTypography>{item.content}</ContentTypography>
                </Grid>
              </React.Fragment>
            ))
          }
        </Grid>
      </CardContent>
    </Card>
  )
}

type TypographyProps = {
  children: ReactNode;
};

const LabelTypography = ({ children } : TypographyProps) => (
  <Typography variant='body1'>
    {children}
  </Typography>
);

const ContentTypography = ({ children } : TypographyProps) => (
  <Typography variant='body2' color="textSecondary">
    {children}
  </Typography>
);