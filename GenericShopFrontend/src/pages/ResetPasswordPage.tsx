import { Button, Card, CardActions, CardContent, Stack, TextField, Typography } from "@mui/material"
import { CSSProperties, useEffect, useState } from "react"
import { useForm } from "react-hook-form"
import z, { date } from "zod";
import { environment, regex } from "../utils/constants";
import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslation } from "react-i18next";
import VisibilityButton from "../components/reusable/VisibilityButton";
import { useLocation, useNavigate } from "react-router-dom";
import { toast } from "sonner";
import handleAxiosException from "../services/apiService";
import { AUTH_PATH } from "../components/singleuse/Routing";
import axios from "axios";

const schema = z.object({
  password: z.string().regex(regex.PASSWORD, 'reset_password.password.not_valid'),
  repeatPassword: z.string()
}).refine(data => data.password === data.repeatPassword, {
  message: 'reset_password.repeatPassword.not_valid',
  path: ['repeatPassword']
});

type ResetPasswordData = z.infer<typeof schema>

type ResetPasswordRequest = {
  password: string
  resetPasswordToken: string
}

type ResetPasswordPageParams = {
  setLoading: (state: boolean) => void
  style: CSSProperties
}

export default function ResetPasswordPage({ setLoading, style } : ResetPasswordPageParams) {
  const fieldStyle = {height: '64px', width: '60%'};
  const { t } = useTranslation();
  const { register, formState, reset, handleSubmit } = useForm<ResetPasswordData>({
    mode: 'onChange',
    resolver: zodResolver(schema)
  });
  const { errors, isValid } = formState;
  const [ passwordVisible, setPasswordVisible ] = useState<boolean>(false)
  const location = useLocation();
  const navigate = useNavigate();
  const [ verificationToken, setVerificationToken ] = useState<string>('');
  
  // Make it render once
  let flag = true;

  useEffect(() => {
    if (flag) {
      const searchParams = new URLSearchParams(location.search);
      const token: string | null = searchParams.get('token')
      
      token && setVerificationToken(token)
      
      if (token !== null) {
        setLoading(true)
        confirmAccount(token)
          .catch(e => {
            handleAxiosException(e)
            navigate(AUTH_PATH)
          })
          .finally(() => setLoading(false))
  
      } else {
        navigate(AUTH_PATH)
        toast.error(t('exception.auth.token.expired'))
      }
      flag = false;
    }
  }, [])

  const confirmAccount = async (token: string) => {
    return await axios.get(`${environment.apiBaseUrl}/account/self/reset-password/validate`, {
      params: {
        token
      }
    })
  }

  const onValid = async (data: ResetPasswordData) => {
    try {
      setLoading(true)
      const request: ResetPasswordRequest = {
        password: data.password,
        resetPasswordToken: verificationToken
      }
      await resetPassword(request)
      navigate(AUTH_PATH)
      toast.success(t('reset_password_success'))

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const resetPassword = async (data: ResetPasswordRequest) => {
    return await axios.put<ResetPasswordRequest>(`${environment.apiBaseUrl}/account/self/reset-password`, data);
  }

  return (
    <Card elevation={10} sx={style}>
      <form onSubmit={handleSubmit(onValid)} noValidate>
      <CardContent>
          <Stack spacing={5} sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '20px' }}>
            <Typography variant='h3' textAlign='center'>{t('reset_password.title')}</Typography>

            <TextField label={t('reset_password.label.password')} {...register('password')}
              type={passwordVisible ? 'text' : 'password'}
              placeholder={t('reset_password.enter.password')}
              error={Boolean(errors.password)}
              helperText={errors.password?.message && t(errors.password.message)}
              sx={fieldStyle}
              autoComplete='true'
              InputProps={{
                endAdornment: <VisibilityButton visible={passwordVisible} onClick={() => setPasswordVisible(!passwordVisible)} />
              }}
            />

            <TextField label={t('reset_password.label.repeatPassword')} {...register('repeatPassword')}
              type={passwordVisible ? 'text' : 'password'}
              placeholder={t('reset_password.enter.repeatPassword')}
              error={Boolean(errors.repeatPassword)}
              helperText={errors.repeatPassword?.message && t(errors.repeatPassword.message)}
              sx={fieldStyle}
              autoComplete='true'
              InputProps={{
                endAdornment: <VisibilityButton visible={passwordVisible} onClick={() => setPasswordVisible(!passwordVisible)} />
              }}
            />
          </Stack>
        </CardContent>

        <CardActions sx={{ justifyContent: 'center', marginBottom: '20px' }}>
          <Stack direction='row' spacing={5}>
            <Button type='submit' variant='contained' disabled={!isValid}>{t('reset_password.submit')}</Button>
            <Button type='button' variant='outlined' color='secondary' onClick={() => reset()}>{t('reset_password.reset')}</Button>
          </Stack>
        </CardActions>
      </form>
    </Card>
  )
}