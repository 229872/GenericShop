import { Button, Card, CardActions, CardContent, Stack, Typography } from "@mui/material";
import { CSSProperties } from "react";
import { useTranslation } from "react-i18next";
import { useNavigate } from "react-router-dom";

type NotFoundPageParams = {
  style: CSSProperties
}

export default function NotFoundPage({ style } : NotFoundPageParams) {
  const { t } = useTranslation();
  const navigate = useNavigate();

  return (
    <Card elevation={10} sx={style}>
      <CardContent>
        <Stack direction='column' spacing={3}>
          <Typography variant='h2' textAlign='center'>404</Typography>

          <Typography variant='h3' textAlign='center'><span style={{ color: 'red'}}>{t('notfound.oops')}</span> {t('notfound.title')}</Typography>

          <Typography variant='h6' textAlign='center'>{t('notfound.description')}</Typography>
        </Stack>

      </CardContent>

      <CardActions sx={{ display: 'flex', justifyContent: 'center', paddingBottom: '20px' }}>
        <Button variant='contained' onClick={() => navigate('/home')}>{t('notfound.button')}</Button>
      </CardActions>
    </Card>
  )
}