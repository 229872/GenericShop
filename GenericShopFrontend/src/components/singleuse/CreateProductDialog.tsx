import { Autocomplete, Button, Dialog, DialogActions, DialogContent, Stack, Step, StepLabel, Stepper, TextField, Typography } from "@mui/material";
import axios from "axios";
import { CSSProperties, useEffect, useState } from "react";
import { Controller, useForm } from "react-hook-form";
import z, { ZodObject } from "zod";
import { environment } from "../../utils/constants";
import { useTranslation } from "react-i18next";
import { getJwtToken } from "../../services/tokenService";
import handleAxiosException from "../../services/apiService";
import { zodResolver } from "@hookform/resolvers/zod";
import { TFunction } from "i18next";

type CreateProductDialogProps = {
  open: boolean
  onClose: () => void
  setLoading: (loading: boolean) => void
  style?: CSSProperties
}

export default function CreateProductDialog({ open, onClose, setLoading, style } : CreateProductDialogProps) {
  const { t } = useTranslation();
  const steps: string[] = ['manage_products.create_product.step.1.title', 'manage_products.create_product.step.2.title'];
  const [ activeStep, setActiveStep ] = useState<1 | 2>(1);
  const [ isStep1Valid, setIsStep1Valid ] = useState<boolean>(false)
  const [ categories, setCategories ] = useState<string[]>([])
  const [ validCategorySchema, setValidCategorySchema ] = useState<any>(undefined)
  const [ newProductCategory, setNewProductCategory ] = useState<string | undefined>(undefined)

  useEffect(() => {
    let supportedValues: string[] = []

    findAllCategories()
      .then(response => {
        setLoading(true);
        supportedValues = response.data;
        setCategories(supportedValues)
        setLoading(false)
      })
      .catch(e => {
        handleAxiosException(e)
        setLoading(false)
      })

    const newSchema = z.object({
      name: z.string({ message: 'manage_products.error.not_supported' }).refine(value => supportedValues.includes(value), {
        message: 'manage_products.error.not_supported'
      })
    })
    setValidCategorySchema(newSchema);
  }, []);


  const findAllCategories = async () => {
    return axios.get(`${environment.apiBaseUrl}/categories`, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const findCategorySchema = async (categoryName: string) => {
    return axios.get(`${environment.apiBaseUrl}/categories/name/${categoryName}`, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const onStep1Valid = async (formData: { name: string }) => {
    console.log(formData)
    setNewProductCategory(formData.name)
    const { data } = await findCategorySchema(formData.name)
    console.log("Category Schema: ", data)
    setActiveStep(2)
  }

  const renderStep = (activeStep: 1 | 2) => {
    switch (activeStep) {
      case 1:
        return validCategorySchema && <Step1 schema={validCategorySchema} categories={categories} t={t} setIsValid={setIsStep1Valid} onValid={onStep1Valid} />
      case 2:
        return
    }
  }

  const renderButtons = (activeStep: 1 | 2) => {
    switch (activeStep) {
      case 1:
        return <Stack direction='row' spacing={2} sx={{ marginTop: '40px' }}>
          <Button type='button' variant='contained' onClick={() => onClose()}>
            <Typography>{t('manage_products.button.back')}</Typography>
          </Button>

          <Button type='submit' form='create_product.pick_category' variant='contained' disabled={!isStep1Valid}>
            <Typography>{t('manage_products.button.next')}</Typography>
          </Button>
        </Stack>

      
      case 2:
        return <Stack direction='row' spacing={2} sx={{ marginTop: '40px' }}>
          <Button type='button' onClick={() => setActiveStep(1)}>
            <Typography>{t('manage_products.button.back')}</Typography>
          </Button>

          <Button type='submit' variant='contained'>
            <Typography>{t('manage_products.button.submit')}</Typography>
          </Button>
        </Stack>
    }
  }

  return (
    <Dialog open={open} onClose={onClose} sx={{ ...style, marginTop: '4vh' }} maxWidth='md'>
        <DialogContent>
            <Stepper activeStep={activeStep - 1}>
              {
                steps.map((label, key) => (
                  <Step key={key}>
                    <StepLabel>{t(label)}</StepLabel>
                  </Step>
                ))
              }
            </Stepper>

            { renderStep(activeStep) }
          </DialogContent>
          <DialogActions>
            { renderButtons(activeStep) }
          </DialogActions>
    </Dialog>
  )
}

type Step1Params = {
   schema: z.ZodType<{name: string}>
   categories: string[]
   t: TFunction<"translation", undefined>
   setIsValid: (isValid: boolean) => void
   onValid: (data: { name: string }) => Promise<void>
}

function Step1({ schema, categories, t, setIsValid, onValid } : Step1Params) {
  
  type CategoryName = z.infer<typeof schema>;
  const { handleSubmit, formState, control } = useForm<CategoryName>({
    resolver: zodResolver(schema),
    mode: "onChange"
  })
  const { errors, isValid } = formState;

  useEffect(() => {
    setIsValid(isValid)
  }, [isValid])

  return (
    <form noValidate onSubmit={handleSubmit(onValid)} id='create_product.pick_category'>
      <Stack spacing={7} sx={{ marginTop: '30px' }}>
        <Controller
          name='name'
          control={control}
          render={({ field }) => (
            <Autocomplete
              options={categories}
              onChange={(e, value) => field.onChange(value)}
              isOptionEqualToValue={(option: any, value: any) => option.value === value.value}
              value={categories.find(option => option === field.value) || ''}
              renderInput={(params) => (
                <TextField
                  {...params}
                  label={t('manage_products.create_product.category.label')}
                  error={Boolean(errors.name)}
                  helperText={errors.name?.message && t(errors.name.message)}
                  placeholder={t('manage_products.create_product.category.enter')}
                />
              )}
            />
          )}
        />
      </Stack>
    </form>
  )
}