import { Autocomplete, Button, Card, CardActions, CardContent, Grid, Stack, Step, StepLabel, Stepper, TextField, Typography } from "@mui/material";
import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { z } from "zod";
import { environment, regex } from "../utils/constants";
import { Controller, UseFormSetValue, useForm } from "react-hook-form";
import VisibilityButton from "../components/reusable/VisibilityButton";
import { zodResolver } from "@hookform/resolvers/zod";
import axios from "axios";
import { toast } from 'sonner'
import { Link } from "react-router-dom";


type RegisterPageParams = {
  setLoading: (state: boolean) => void
}

export default function RegisterPage({setLoading}: RegisterPageParams) {
  const { t } = useTranslation();
  const [ isStep1Valid, setIsStep1Valid ] = useState<boolean>(false)
  const [ isStep2Valid, setIsStep2Valid ] = useState<boolean>(false)
  const [ step1Data, setStep1Data ] = useState<RegisterStep1>();
  const [ step2Data, setStep2Data ] = useState<RegisterStep2>();
  const steps: string[] = ['register.step.1.title', 'register.step.2.title', 'register.step.3.title'];
  const [ activeStep, setActiveStep ] = useState<1 | 2 | 3>(1);
  const { setValue, getValues } = useForm<RegisterAccountData>({
    resolver: zodResolver(registerSchema)
  })

  const onValid = async () => {
    try {
      setLoading(true)
      const data: RegisterAccountData = getValues()
      await registerAccount(data);
      setActiveStep(3)

    } catch (e: any) {
      if (e.response && e.response.data) {
        toast.error(t(e.response.data.message))
      } else {
        toast.error(t('error'))
      }

    } finally {
      setLoading(false)
    }
  }

  const registerAccount = async (data: RegisterAccountData) => {
    return await axios.post<RegisterAccountData>(`${environment.apiBaseUrl}/account/self/register`, data);
  }
  
  const renderStep = (activeStep: 1 | 2 | 3) => {
    switch (activeStep) {
      case 1:
        return <Step1 setIsStepValid={setIsStep1Valid} setValue={setValue} setActiveStep={setActiveStep} data={step1Data} setData={setStep1Data} />
      case 2:
        return <Step2 setIsStepValid={setIsStep2Valid} setValue={setValue} onValid={onValid} data={step2Data} setData={setStep2Data} />
      case 3:
        return <Step3 />
    }
  }

  return (
    <>
      <Card elevation={20} sx={{ margin: '13vh 25vw', height: '70vh' }}>
        <CardContent>
          <Stepper activeStep={activeStep - 1}>
            {
              steps.map((label, key) => (
                <Step key={key}>
                  <StepLabel>{t(label)}</StepLabel>
                </Step>
              ))
            }
          </Stepper>

          {renderStep(activeStep)}

        </CardContent>

        <CardActions sx={{ justifyContent: 'center', marginBottom: '20px' }}>
          {activeStep == 1 && (
            <Button type='submit' form='register_step1_form' disabled={!isStep1Valid} variant='contained'>
              <Typography>{t('register.step.1.action.next_step')}</Typography>
            </Button>
          )}

          {activeStep == 2 && (
            <Stack direction='row' spacing={10} sx={{ marginTop: '40px' }}>
              <Button type='button' onClick={() => setActiveStep(1) }>
                <Typography>{t('register.step.2.action.back')}</Typography>
              </Button>

              <Button type='submit' form='register_step2_form' disabled={!isStep2Valid} variant='contained'>
                <Typography>{t('register.step.2.action.submit')}</Typography>
              </Button>
            </Stack>
          )}
        </CardActions>
      </Card>
    </>
  )
}





const step1Schema = z.object({
  login: z.string().regex(regex.LOGIN, 'register.step.1.error.login'),
  email: z.string().email('register.step.1.error.email'),
  password: z.string().regex(regex.PASSWORD, 'register.step.1.error.password'),
  locale: z.enum(environment.supportedLanguages as readonly [string, ...string[]], {message: 'register.step.1.error.language'})
});

type RegisterStep1 = z.infer<typeof step1Schema>;

type Step1Params = {
  setIsStepValid: (state: boolean) => void
  setValue: UseFormSetValue<RegisterAccountData>
  setActiveStep: (step: 1 | 2 | 3) => void
  data: RegisterStep1 | undefined
  setData: (data: RegisterStep1) => void
}

function Step1({ setIsStepValid, setValue: setExternalValue, setActiveStep, data, setData }: Step1Params) {
  const fieldStyleForStep1 = {height: '64px', width: '60%'}
  const { t } = useTranslation();
  const [ passwordVisible, setPasswordVisible ] = useState<boolean>(false);
  const { register, formState, handleSubmit, setValue, control, trigger } = useForm<RegisterStep1>({
    mode: "onBlur", resolver: zodResolver(step1Schema)
  });
  const { errors, isValid } = formState;

  useEffect(() => {
    if (data !== undefined) {
      setValue('login', data.login)
      setValue('password', data.password)
      setValue('email', data.email)
      setValue('locale', data.locale)
      trigger().then(isValid => setIsStepValid(isValid))
    }
  }, [data])
  
  useEffect(() => {
    setIsStepValid(isValid);
  }, [isValid, setIsStepValid]);

  const onValid = (data: RegisterStep1) => {
    setExternalValue('login', data.login)
    setExternalValue('password', data.password)
    setExternalValue('email', data.email)
    setExternalValue('locale', data.locale)
    setData(data)
    setActiveStep(2)
  }

  return (
    <>
      <Stack spacing={5} sx={{ margin: '35px' }}>
        <Typography variant='h3' textAlign='center'><b>{t('register.title')}</b></Typography>
      </Stack>

      <form id='register_step1_form' onSubmit={handleSubmit(onValid)} noValidate>
        <Stack direction='column' spacing={5} sx={{ width: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: '30px' }}>
          <TextField label={t('register.step.1.label.login')} {...register('login')}
            error={Boolean(errors.login?.message)}
            placeholder={t('register.step.1.enter.login')}
            helperText={errors.login?.message && t(errors.login.message)}
            sx={fieldStyleForStep1}
          />

          <TextField label={t('register.step.1.label.password')} {...register('password')} type={passwordVisible ? 'text' : 'password'}
            error={Boolean(errors.password?.message)}
            placeholder={t('register.step.1.enter.password')}
            helperText={errors.password?.message && t(errors.password.message)}
            sx={fieldStyleForStep1}
            InputProps={{
              endAdornment: <VisibilityButton visible={passwordVisible} onClick={() => setPasswordVisible(!passwordVisible)} />
            }}
          />

          <TextField label={t('register.step.1.label.email')} {...register('email')} type='email'
            error={Boolean(errors.email?.message)}
            placeholder={t('register.step.1.enter.email')}
            helperText={errors.email?.message && t(errors.email.message)}
            sx={fieldStyleForStep1}
          />

          <Controller
            name='locale'
            control={control}
            render={({ field }) => (
              <Autocomplete
                options={environment.supportedLanguages}
                sx={fieldStyleForStep1}
                onChange={(e, value) => field.onChange(value)}
                isOptionEqualToValue={(option: any, value: any) => option.value === value.value}
                value={environment.supportedLanguages.find(option => option === field.value) || ''}
                renderInput={(params) => (
                  <TextField
                    {...params}
                    label={t('register.step.1.label.language')}
                    error={Boolean(errors.locale)}
                    helperText={errors.locale?.message && t(errors.locale.message)}
                    placeholder={t('register.step.1.enter.language')}
                  />
                )}
              />
            )}
          />
        </Stack>
      </form>
    </>
  )
}





const step2Schema = z.object({
  firstName: z.string().regex(regex.CAPITALIZED, 'register.step.2.error.firstname'),
  lastName: z.string().regex(regex.CAPITALIZED, 'register.step.2.error.lastname'),
  address: z.object({
    postalCode: z.string().regex(regex.POSTAL_CODE, 'register.step.2.error.postal_code'),
    country: z.string().regex(regex.CAPITALIZED, 'register.step.2.error.country'),
    city: z.string().regex(regex.CAPITALIZED, 'register.step.2.error.city'),
    street: z.string().regex(regex.CAPITALIZED, 'register.step.2.error.street'),
    houseNumber: z.number({message: 'register.step.2.error.house_number'}).int('register.step.2.error.house_number').positive('register.step.2.error.house_number')
  })
});

type RegisterStep2 = z.infer<typeof step2Schema>

type Step2Params = {
  setIsStepValid: (state: boolean) => void
  setValue: UseFormSetValue<RegisterAccountData>
  onValid: () => void
  data: RegisterStep2 | undefined
  setData: (data: RegisterStep2) => void
}

function Step2({ setIsStepValid, setValue: setExternalValue, onValid: sumbitMainForm, data, setData }: Step2Params) {
  const fieldStyleForStep2 = {height: '64px', width: '100%'};
  const { t } = useTranslation();
  const { register, formState, handleSubmit, setValue, trigger, getValues } = useForm<RegisterStep2>({
    mode: "onBlur",
    resolver: zodResolver(step2Schema),
    defaultValues: {
      address: {
        houseNumber: 0
      }
    }
  });
  const { errors, isValid } = formState;

  useEffect(() => {
    if (data !== undefined) {
      setValue('firstName', data.firstName)
      setValue('lastName', data.lastName)
      setValue('address', data.address)
      trigger().then(isValid => setIsStepValid(isValid))
    }
  }, [])

  useEffect(() => {
    setIsStepValid(isValid);
  }, [isValid, setIsStepValid]);

  useEffect(() => {
    const data = getValues()
    setData(data);
  }, [getValues().firstName, getValues().lastName, getValues().address, setData]);

  const onValid = (data: RegisterStep2) => {
    setExternalValue('firstName', data.firstName)
    setExternalValue('lastName', data.lastName)
    setExternalValue('address', data.address)
    setData(data)
    sumbitMainForm()
  }

  return (
    <>
      <Stack spacing={5} sx={{ margin: '35px' }}>
        <Typography variant='h3' textAlign='center'><b>{t('register.title')}</b></Typography>
      </Stack>

      <form id='register_step2_form' onSubmit={handleSubmit(onValid)} noValidate>
        <Grid container spacing={5}>
          <Grid item xs={12} sm={6}>
            <TextField label={t('register.step.2.label.firstname')} {...register('firstName')}
              error={Boolean(errors.firstName?.message)}
              placeholder={t('register.step.2.enter.firstname')}
              helperText={errors.firstName?.message && t(errors.firstName.message)}
              sx={fieldStyleForStep2}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField label={t('register.step.2.label.lastname')} {...register('lastName')}
              error={Boolean(errors.lastName?.message)}
              placeholder={t('register.step.2.enter.lastname')}
              helperText={errors.lastName?.message && t(errors.lastName.message)}
              sx={fieldStyleForStep2}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField label={t('register.step.2.label.postal_code')} {...register('address.postalCode')}
              error={Boolean(errors.address?.postalCode?.message)}
              placeholder={t('register.step.2.enter.postal_code')}
              helperText={errors.address?.postalCode?.message && t(errors.address.postalCode.message)}
              sx={fieldStyleForStep2}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField label={t('register.step.2.label.country')} {...register('address.country')}
              error={Boolean(errors.address?.country?.message)}
              placeholder={t('register.step.2.enter.country')}
              helperText={errors.address?.country?.message && t(errors.address.country.message)}
              sx={fieldStyleForStep2}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField label={t('register.step.2.label.city')} {...register('address.city')}
              error={Boolean(errors.address?.city?.message)}
              placeholder={t('register.step.2.enter.city')}
              helperText={errors.address?.city?.message && t(errors.address.city.message)}
              sx={fieldStyleForStep2}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField label={t('register.step.2.label.street')} {...register('address.street')}
              error={Boolean(errors.address?.street?.message)}
              placeholder={t('register.step.2.enter.street')}
              helperText={errors.address?.street?.message && t(errors.address.street.message)}
              sx={fieldStyleForStep2}
            />
          </Grid>
          <Grid item xs={12} sm={6}>
            <TextField label={t('register.step.2.label.house_number')} {...register('address.houseNumber', { valueAsNumber: true })} type='number'
              error={Boolean(errors.address?.houseNumber?.message)}
              placeholder={t('register.step.2.enter.house_number')}
              helperText={errors.address?.houseNumber?.message && t(errors.address.houseNumber.message)}
              sx={fieldStyleForStep2}
            />
          </Grid>
        </Grid>
      </form>
    </>
  )
}





function Step3() {
  const { t } = useTranslation()

  return (
    <>
      <Stack spacing={5} sx={{ margin: '35px' }}>
        <Typography variant='h3' textAlign='center'><b>{t('register.step.3.label')}</b></Typography>
      </Stack>

      <Stack direction='column' spacing={5}>
        <Typography variant='body1'>
          {t('register.step.3.content.1')}
          <br />
          <br />
          {t('register.step.3.content.2')}
        </Typography>

        <Typography variant='body1'>
          {t('register.step.3.content.3')}
          <Link to='/auth'>{t('register.step.3.link')}</Link>
        </Typography>
      </Stack>
    </>
  )
}

const combinedSchema = step1Schema.merge(step2Schema);

const registerSchema = z.object({
  login: combinedSchema.shape.login,
  email: combinedSchema.shape.email,
  password: combinedSchema.shape.password,
  locale: combinedSchema.shape.locale,
  firstName: combinedSchema.shape.firstName,
  lastName: combinedSchema.shape.lastName,
  address: combinedSchema.shape.address
});

type RegisterAccountData = z.infer<typeof registerSchema>;