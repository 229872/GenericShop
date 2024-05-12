import { Box, Button, Card, CardActions, CardContent, Grid, Stack, Step, StepLabel, Stepper, TextField, Typography } from "@mui/material";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { z } from "zod";
import { regex } from "../utils/constants";
import { useForm } from "react-hook-form";
import VisibilityButton from "../components/reusable/VisibilityButton";
import { zodResolver } from "@hookform/resolvers/zod";

const step1Schema = z.object({
  login: z.string().regex(regex.LOGIN, 'Login is in wrong format'),
  email: z.string().email('Email is not valid'),
  password: z.string().regex(regex.PASSWORD, 'Password is not valid'),
  locale: z.enum(['pl', 'en'], {message: 'Language must be pl or en'})
});
type RegisterStep1 = z.infer<typeof step1Schema>;

const step2Schema = z.object({
  firstName: z.string().regex(regex.CAPITALIZED, 'First name must start with capital letter'),
  lastName: z.string().regex(regex.CAPITALIZED, 'Last name must start with capital letter'),
  address: z.object({
    postalCode: z.string().regex(regex.POSTAL_CODE),
    country: z.string().regex(regex.CAPITALIZED, 'Country must start with capital letter'),
    city: z.string().regex(regex.CAPITALIZED, 'City must start with capital letter'),
    street: z.string().regex(regex.CAPITALIZED, 'Street must start with capital letter'),
    houseNumber: z.number({message: 'House number must be number'}).int('House number must be integer value').positive('House number must be positive')
  })
})
type RegisterStep2 = z.infer<typeof step2Schema>;

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

type RegisterAccountData = z.infer<typeof registerSchema>

const steps: string[] = ['register.step.1.title', 'register.step.2.title', 'register.step.3.title'];



export default function RegisterPage() {
  const fieldStyleForStep1 = {height: '64px', width: '60%'}
  const fieldStyleForStep2 = {height: '64px', width: '100%'};
  const { t } = useTranslation();
  const [ passwordVisible, setPasswordVisible ] = useState<boolean>(false);
  const [ activeStep, setActiveStep ] = useState<number>(1);
  const { register, formState, handleSubmit, getValues } = useForm<RegisterAccountData>({
    mode: "onChange",
    resolver: zodResolver(registerSchema),
    defaultValues: {
      address: {
        houseNumber: 0
      }
    }
  });
  const { errors, isValid } = formState;

  const isStep1Valid = (data: RegisterAccountData): boolean => {
    try {
      step1Schema.parse(data)
      return true;
    } catch (error) {
      return false;
    }
  }

  return (
    <>
      <Card elevation={20} sx={{ margin: '17vh 25vw' }}>
        <form noValidate>
          <CardContent>
            <Stepper activeStep={activeStep}>
              {
                steps.map((label, key) => (
                  <Step key={key}>
                    <StepLabel>{t(label)}</StepLabel>
                  </Step>
                ))
              }
            </Stepper>

            <Stack spacing={5} sx={{ margin: '35px' }}>
              <Typography variant='h3' textAlign='center'><b>{t('register.title')}</b></Typography>
            </Stack>


            {activeStep === 0 && (
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

                <TextField label={t('register.step.1.label.email')} {...register('email')}
                  error={Boolean(errors.email?.message)}
                  placeholder={t('register.step.1.enter.email')}
                  helperText={errors.email?.message && t(errors.email.message)}
                  sx={fieldStyleForStep1}
                />

                <TextField label={t('register.step.1.label.language')} {...register('locale')}
                  error={Boolean(errors.locale?.message)}
                  placeholder={t('register.step.1.enter.language')}
                  helperText={errors.locale?.message && t(errors.locale.message)}
                  sx={fieldStyleForStep1}
                />
              </Stack>
            )}

            {activeStep === 1 && (
              <Grid container spacing={4}>
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
                  <TextField label={t('register.step.2.label.house_number')} {...register('address.houseNumber', { valueAsNumber: true })}
                    error={Boolean(errors.address?.houseNumber?.message)}
                    placeholder={t('register.step.2.enter.house_number')}
                    helperText={errors.address?.houseNumber?.message && t(errors.address.houseNumber.message)}
                    sx={fieldStyleForStep2}
                  />
                </Grid>
              </Grid>
            )}
          </CardContent>

          <CardActions sx={{ justifyContent: 'center', marginBottom: '20px' }}>
            {activeStep == 0 && (
              <Button type='button' disabled={!isStep1Valid(getValues())} onClick={() => setActiveStep(1)}>
                <Typography>{t('register.step.1.action.next_step')}</Typography>
              </Button>
            )}

            {activeStep == 1 && (
              <Stack direction='row' spacing={20}>
                <Button type='button' onClick={() => setActiveStep(0)}>
                  <Typography>{t('register.step.2.action.back')}</Typography>
                </Button>

                <Button type='submit' disabled={!isValid} onClick={() => setActiveStep(2)} variant='contained'>
                  <Typography>{t('register.step.2.action.submit')}</Typography>
                </Button>
              </Stack>
            )}
          </CardActions>
        </form>
      </Card>
    </>
  )
}