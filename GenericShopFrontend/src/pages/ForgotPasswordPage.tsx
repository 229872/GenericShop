import { zodResolver } from "@hookform/resolvers/zod"
import { Button, Card, CardActions, CardContent, Stack, TextField, Typography } from "@mui/material"
import axios from "axios"
import { CSSProperties } from "react"
import { useForm } from "react-hook-form"
import { useTranslation } from "react-i18next"
import z from 'zod'
import { environment } from "../utils/constants"
import handleAxiosException from "../services/apiService"
import { useNavigate } from "react-router-dom"
import { toast } from "sonner"
import { AUTH_PATH } from "../components/singleuse/Routing"


const schema = z.object({
  email: z.string().email('forgot_password.email.not_valid')
})

type ForgotPasswordRequest = z.infer<typeof schema>

type ForgotPasswordPageParams = {
  setLoading: (state: boolean) => void
  style: CSSProperties
}

export default function ForgotPasswordPage({ setLoading, style } : ForgotPasswordPageParams) {
  const fieldStyle = {height: '64px', width: '60%'};
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { register, formState, handleSubmit, reset } = useForm<ForgotPasswordRequest>({
    mode: 'onChange',
    resolver: zodResolver(schema)
  })
  const { errors, isValid } = formState;

  const onValid = async (data: ForgotPasswordRequest) => {
    try {
      setLoading(true)
      await sendEmail(data)
      toast.success(t('forgot_password.success'))
      reset();
    } catch (e: any) {
      handleAxiosException(e)
      navigate(AUTH_PATH)

    } finally {
      setLoading(false)
    }
  }

  const sendEmail = async (data: ForgotPasswordRequest) => {
    return axios.put<ForgotPasswordRequest>(`${environment.apiBaseUrl}/account/self/forgot-password`, data)
  }

  return (
    <Card elevation={10} sx={style}>
      <form onSubmit={handleSubmit(onValid)} noValidate>
        <CardContent>
          <Stack spacing={5} sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '20px' }}>
            <Typography variant='h3' textAlign='center'>{t('forgot_password.title')}</Typography>

            <TextField label={t('forgot_password.label.email')} {...register('email')}
              placeholder={t('forgot_password.enter.email')}
              error={Boolean(errors.email)}
              helperText={errors.email?.message && t(errors.email.message)}
              sx={fieldStyle}
            />
          </Stack>
        </CardContent>

        <CardActions sx={{ justifyContent: 'center', marginBottom: '20px' }}>
          <Stack direction='row' spacing={5}>
            <Button type='submit' variant='contained' disabled={!isValid}>{t('forgot_password.submit')}</Button>
            <Button type='button' variant='outlined' color='secondary' onClick={() => reset()}>{t('forgot_password.reset')}</Button>
          </Stack>
        </CardActions>
      </form>
    </Card>
  )
}