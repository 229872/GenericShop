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


const schema = z.object({
  email: z.string().email('reset_password.email.not_valid')
})

type ResetPasswordRequest = z.infer<typeof schema>

type ForgotPasswordPageParams = {
  setLoading: (state: boolean) => void
  style: CSSProperties
}

export default function ResetPasswordPage({ setLoading, style } : ForgotPasswordPageParams) {
  const fieldStyle = {height: '64px', width: '60%'};
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { register, formState, handleSubmit, reset } = useForm<ResetPasswordRequest>({
    mode: 'onChange',
    resolver: zodResolver(schema)
  })
  const { errors, isValid } = formState;

  const onValid = async (data: ResetPasswordRequest) => {
    try {
      setLoading(true)
      await sendEmail(data)
      toast.success(t('reset_password.success'))
    } catch (e: any) {
      handleAxiosException(e)
      navigate('/auth')

    } finally {
      setLoading(false)
    }
  }

  const sendEmail = async (data: ResetPasswordRequest) => {
    axios.put<ResetPasswordRequest>(`${environment.apiBaseUrl}/account/self/forgot-password`, data)
  }

  return (
    <Card elevation={10} sx={style}>
      <form onSubmit={handleSubmit(onValid)} noValidate>
        <CardContent>
          <Stack spacing={5} sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '20px' }}>
            <Typography variant='h3' textAlign='center'>{t('reset_password.title')}</Typography>

            <TextField label={t('reset_password.label.email')} {...register('email')}
              placeholder={t('reset_password.enter.email')}
              error={Boolean(errors.email)}
              helperText={errors.email?.message && t(errors.email.message)}
              sx={fieldStyle}
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