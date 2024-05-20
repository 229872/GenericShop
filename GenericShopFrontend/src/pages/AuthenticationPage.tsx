import { Button, Card, CardActions, CardContent, Stack, TextField, Typography } from "@mui/material";
import z from 'zod';
import { useForm } from "react-hook-form"
import { zodResolver } from '@hookform/resolvers/zod';
import { useTranslation } from "react-i18next";
import axios from 'axios';
import { environment, regex } from "../utils/constants";
import { decodeJwtToken, saveJwtToken, saveLocale, saveRefreshToken } from "../services/tokenService";
import { Link, useNavigate } from "react-router-dom";
import { toast } from 'sonner'
import { SessionDialogsActions, Tokens } from "../utils/types";
import { useState } from "react";
import VisibilityButton from "../components/reusable/VisibilityButton";
import handleAxiosException from "../services/apiService";
import { HOME_PATH } from "../components/singleuse/Routing";

const schema = z.object({
  login: z.string().regex(regex.LOGIN, 'authentication.login.not_valid'),
  password: z.string().regex(regex.PASSWORD, 'authentication.password.not_valid')
});

type Credentials = z.infer<typeof schema>;



export default function AuthenticationPage({ showTokenExpiredDialogAfterTimeout, showExtendSessionDialogAfterTimeout, setLoading, style }: SessionDialogsActions) {
  const fieldStyle = {height: '64px', width: '60%'};
  const [ isHover, setIsHover ] = useState(false)
  const linkStyle = { color: 'black', textDecoration: isHover ? 'underline' : 'none' }
  const {t} = useTranslation();
  const navigate = useNavigate();
  const [ passwordVisible, setPasswordVisible ] = useState<boolean>(false)
  const { register, formState, handleSubmit, reset } = useForm<Credentials>({
    mode: 'onChange',
    resolver: zodResolver(schema)   
  });
  const { errors, isValid } = formState;


  const onValid = async (credentials: Credentials) => {
    try {
      setLoading(true)
      const { data: {token, refreshToken} } = await sendCredentials(credentials)
      const lang = decodeJwtToken(token)?.lang;
      saveDataInLocalStorage(token, refreshToken, lang)
      reset()
      navigate(HOME_PATH)
      toast.success(t('authentication.toast.success'))
      showTokenExpiredDialogAfterTimeout()
      showExtendSessionDialogAfterTimeout()

    } catch (e: any) {
      handleAxiosException(e)
      
    } finally {
      setLoading(false)
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
    <Card elevation={2} sx={style}>
      <form onSubmit={handleSubmit(onValid)} noValidate>
        <CardContent>
          <Stack spacing={5} sx={{margin: '35px'}}>
            <Typography variant='h3' textAlign='center'><b>{t('app.name')}</b></Typography>
            <Typography variant='h5' textAlign='center'>{t('authentication.please.login')}</Typography>
          </Stack>

          <Stack spacing={5} sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '20px' }}>
            <TextField label={t('authentication.label.login')} {...register('login')}
              error={Boolean(errors.login?.message)}
              placeholder={t('authentication.enter.login')}
              helperText={errors.login?.message && t(errors.login.message)}
              autoComplete='true'
              sx={fieldStyle}
            />

            <TextField label={t('authentication.label.password')} {...register('password')} type={passwordVisible ? 'text' : 'password'}
              error={Boolean(errors.password?.message)}
              placeholder={t('authentication.enter.password')}
              helperText={errors.password?.message && t(errors.password.message)}
              sx={fieldStyle}
              InputProps={{
                endAdornment: <VisibilityButton visible={passwordVisible} onClick={() => setPasswordVisible(!passwordVisible)} />
              }}
              autoComplete='true'
            />

            <Link to='/auth/forgot-password' style={linkStyle} onMouseEnter={() => setIsHover(true)} onMouseLeave={() => setIsHover(false)}>
              <Typography variant='h6'>
                {t('authentication.forgot.password')}
              </Typography>
            </Link>
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