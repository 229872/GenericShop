import { Button, Card, CardActions, CardContent, Stack, TextField, Typography } from "@mui/material";
import z from 'zod';
import { useForm } from "react-hook-form"
import { zodResolver } from '@hookform/resolvers/zod';
import { useTranslation } from "react-i18next";
import axios from 'axios';
import { environment } from "../utils/constants";
import { decodeJwtToken, saveJwtToken, saveLocale, saveRefreshToken } from "../utils/tokenService";
import { useNavigate } from "react-router-dom";
import { toast } from 'sonner'

const schema = z.object({
  login: z.string().regex(/^[a-zA-Z][a-zA-Z0-9]*$/, 'authentication.login.not_valid'),
  password: z.string().regex(/^(?=.*[A-Z])(?=.*[!@#$%^&+=]).*$/, 'authentication.password.not_valid')
});

type Credentials = z.infer<typeof schema>;

type Tokens = {
  token: string
  refreshToken: string
}


export default function AuthenticationPage() {
  const fieldStyle = {height: '64px', width: '60%'};
  const { register, formState, handleSubmit, reset } = useForm<Credentials>({
    mode: 'onChange',
    resolver: zodResolver(schema)   
  });
  const { errors, isValid } = formState;  
  const {t} = useTranslation();
  const navigate = useNavigate();

  const onValid = async (credentials: Credentials) => {
    try {
      const { data: {token, refreshToken} } = await sendCredentials(credentials)

      const lang = decodeJwtToken(token)?.lang;
      saveDataInLocalStorage(token, refreshToken, lang)
      reset()
      navigate('/home')
      toast.success(t('authentication.toast.success'))

    } catch (e: any) {

      if (e.response && e.response.data) {
        const { message } = e.response.data;
        toast.error(t(message))
      } else {
        toast.error(t('error'))
      }
    }
  }

  const saveDataInLocalStorage = (token: string, refreshToken: string, language: string | undefined) => {
    saveJwtToken(token)
    saveRefreshToken(refreshToken)
  
    if (language) {
      saveLocale(language)
    }
  }

  const sendCredentials = async (data: Credentials) => {
    return axios.post<Tokens>(`${environment.apiBaseUrl}/auth`, data)
  }

  return (
    <Card elevation={2} sx={{margin: '20vh 25vw'}}>
      <form onSubmit={handleSubmit(onValid)} noValidate>
        <CardContent>
          <Stack spacing={5} sx={{margin: '35px'}}>
            <Typography variant='h3' textAlign='center'><b>{t('authentication.app.name')}</b></Typography>
            <Typography variant='h5' textAlign='center'>{t('authentication.app.please.login')}</Typography>
          </Stack>

          <Stack spacing={5} sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
            <TextField label={t('authentication.label.login')} {...register('login')}
              error={Boolean(errors.login?.message)}
              placeholder={t('authentication.enter.login')}
              helperText={errors.login?.message && t(errors.login.message)}
              sx={fieldStyle}
            />

            <TextField label={t('authentication.label.password')} {...register('password')} type='password'
              error={Boolean(errors.password?.message)}
              placeholder={t('authentication.enter.password')}
              helperText={errors.password?.message && t(errors.password.message)}
              sx={fieldStyle}
            />
          </Stack>
        </CardContent>

        <CardActions sx={{justifyContent: 'center', marginBottom: '20px'}}>
          <Button type='submit' disabled={!isValid}>
            <Typography>{t('authentication.button.login')}</Typography>
          </Button>
        </CardActions>
      </form>
    </Card>
  )
}