import { Autocomplete, Button, Checkbox, Dialog, DialogActions, DialogContent, FormControlLabel, Stack, Step, StepLabel, Stepper, TextField, Typography } from "@mui/material";
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
import { toast } from "sonner";
import { CheckBox } from "@mui/icons-material";
import { camelCaseToWords, camelCaseToWordsStartingWillLowerCase } from "../../services/textService";

type SchemaField = {
  property: string;
  type: 'NUMBER' | 'BIG_NUMBER' | 'LOGICAL_VALUE' | 'TEXT' | 'FRACTIONAL_NUMBER';
  nullable: 'YES' | 'NO';
  maxlength?: number;
}

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
  const [ isStep2Valid, setIsStep2Valid ] = useState<boolean>(false)
  const [ categories, setCategories ] = useState<string[]>([])
  const [ validCategorySchema, setValidCategorySchema ] = useState<any>(undefined)
  const [ newProductCategory, setNewProductCategory ] = useState<string | undefined>(undefined)
  const [ categorySchema, setCategorySchema ] = useState<SchemaField[]>([])
  const [ categoryZodSchema, setCategoryZodSchema] = useState<any>(null);


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
    setNewProductCategory(formData.name)
    const { data } = await findCategorySchema(formData.name)
    console.log("Category Schema: ", data)
    setCategorySchema(data)
    const generateCategoryZodSchema = (categorySchema: SchemaField[]) => {
      const schemaObject: any = {};
      schemaObject['name'] = z.string({ message: 'manage_products.create_product.error.property.text' }).min(1, 'manage_products.create_product.error.name.required')
      schemaObject['price'] = z.number({ message: 'manage_products.create_product.error.property.number' }).positive('manage_products.create_product.error.price.positive')
      schemaObject['quantity'] = z.number({ message: 'manage_products.create_product.error.property.number' }).positive('manage_products.create_product.error.quantity.positive')
      schemaObject['imageUrl'] = z.string({ message: 'manage_products.create_product.error.property.text' }).nullable()
      categorySchema.forEach((field: SchemaField) => {
        switch (field.type) {
          case 'NUMBER':
            schemaObject[field.property] = field.nullable === 'NO' ? z.number({ message: 'manage_products.create_product.error.property.required' }) 
            : z.number().nullable();
            break;
          case 'BIG_NUMBER':
            schemaObject[field.property] = field.nullable === 'NO' ? z.bigint({ message: 'manage_products.create_product.error.property.required' }) 
            : z.bigint({ message: 'manage_products.create_product.error.property.number' }).nullable();
            break;
          case 'TEXT':
            schemaObject[field.property] = field.nullable === 'NO'
              ? z.string({ message: 'manage_products.create_product.error.property.text' })
                .min(1, 'manage_products.create_product.error.property.required')
                .max(field.maxlength ?? Infinity, 'manage_products.create_product.error.property.text.max')
              : z.string({ message: 'manage_products.create_product.error.property.text' })
                .max(field.maxlength ?? Infinity, 'manage_products.create_product.error.property.text.max').nullable();
            break;
          case 'LOGICAL_VALUE':
            schemaObject[field.property] = field.nullable === 'NO' ? z.boolean({ message: 'manage_products.create_product.error.property.required' }) : z.boolean().nullable();
            break;
          case 'FRACTIONAL_NUMBER':
            schemaObject[field.property] = field.nullable === 'NO' ? z.number({ message: 'manage_products.create_product.error.property.required' }) 
            : z.number({ message: 'manage_products.create_product.error.property.number' }).nullable();
            break;
        }
      });
      return z.object(schemaObject);
    };

    setCategoryZodSchema(generateCategoryZodSchema(data));
    setActiveStep(2)
  }

  const renderStep = (activeStep: 1 | 2) => {
    switch (activeStep) {
      case 1:
        return validCategorySchema &&
          <Step1 schema={validCategorySchema} categories={categories} t={t} setIsValid={setIsStep1Valid} onValid={onStep1Valid} />
      case 2:
        return categorySchema.length > 0 && newProductCategory && 
          <Step2 categorySchema={categorySchema} setLoading={setLoading} t={t} setIsValid={setIsStep2Valid} zodSchema={categoryZodSchema} newProductCategory={newProductCategory} onClose={onClose} />
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

          <Button type='submit' form='create_product.create' variant='contained' disabled={!isStep2Valid}>
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
                  required
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



type Step2Params = {
  t: TFunction<"translation", undefined>
  setLoading: (state: boolean) => void
  categorySchema: SchemaField[]
  setIsValid: (isValid: boolean) => void
  zodSchema: any
  newProductCategory: string
  onClose: () => void
}

function Step2({ t, setLoading, categorySchema, setIsValid, zodSchema, newProductCategory, onClose } : Step2Params) {
  const fieldStyle = { height: '80px' }
  type FormType = z.infer<typeof zodSchema>
  const { register, handleSubmit, formState, control } = useForm<FormType>({
    resolver: zodResolver(zodSchema),
    mode: 'onChange',
    defaultValues: categorySchema.reduce((acc, field) => {
      acc[field.property] = field.nullable === 'NO' ? '' : '';
      return acc;
    }, {} as Record<string, any>),
  });
  const { errors, isValid } = formState;

  useEffect(() => {
    setIsValid(isValid)
  }, [isValid])

  const onValid = async (data: any) => {
    const { name, price, quantity, imageUrl, ...otherProperties } = data;
    const product = {
      name, price, quantity, imageUrl,
      categoryName: newProductCategory,
      categoryProperties: otherProperties
    }
    
    try {
      setLoading(true)
      await createProduct(product)
      toast.success('Product created successfully')
      onClose()

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const createProduct = async (product: any) => {
    return axios.post(`${environment.apiBaseUrl}/products`, product, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  return (
    <form noValidate onSubmit={handleSubmit(onValid)} id='create_product.create'>
      <Stack spacing={4} sx={{ marginTop: '30px' }}>
        <TextField
          required
          {...register('name')}
          label={t('manage_products.create_product.label.name')}
          placeholder={t('manage_products.create_product.enter.name')}
          error={Boolean(errors.name)}
          fullWidth
          helperText={errors.name?.message && t(errors.name.message as string)}
          sx={{ ...fieldStyle }}
        />

        <TextField
          required
          {...register('price', { valueAsNumber: true })}
          type='number'
          label={t('manage_products.create_product.label.price')}
          placeholder={t('manage_products.create_product.enter.price')}
          error={Boolean(errors.price)}
          fullWidth
          helperText={errors.price?.message && t(errors.price.message as string)}
          sx={{ ...fieldStyle }}
        />

        <TextField
          required
          {...register('quantity', { valueAsNumber: true })}
          type='number'
          label={t('manage_products.create_product.label.quantity')}
          placeholder={t('manage_products.create_product.enter.quantity')}
          error={Boolean(errors.quantity)}
          fullWidth
          helperText={errors.quantity?.message && t(errors.quantity.message as string)}
          sx={{ ...fieldStyle }}
        />

        <TextField
          {...register('imageUrl')}
          label={t('manage_products.create_product.label.image_url')}
          placeholder={t('manage_products.create_product.enter.image_url')}
          error={Boolean(errors.imageUrl)}
          fullWidth
          helperText={errors.imageUrl?.message && t(errors.imageUrl.message as string)}
          sx={{ ...fieldStyle }}
        />

        {categorySchema.map((field: SchemaField) => (
          <Controller
            key={field.property}
            name={field.property}
            control={control}
            render={({ field: controllerField, fieldState }) => {
              if (field.type === 'LOGICAL_VALUE') {
                return (
                  <FormControlLabel
                    control={
                      <Checkbox
                        {...controllerField}
                        checked={controllerField.value || false}
                        onChange={(e) => controllerField.onChange(e.target.checked)}
                      />
                    }
                    label={camelCaseToWords(field.property)}
                    sx={{...fieldStyle}}
                  />
                );
              }
              const inputType =
                field.type === 'NUMBER' || field.type === 'BIG_NUMBER' || field.type === 'FRACTIONAL_NUMBER'
                  ? 'number'
                  : 'text';

                  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
                    let value: any = e.target.value;
                    if ((field.type === 'NUMBER' || field.type === 'FRACTIONAL_NUMBER') && value !== '') {
                      value = parseFloat(value);
                      if (isNaN(value)) {
                        value = '';
                      }
                    }
                    controllerField.onChange(value);
                  };
      

              return (
                <TextField
                  required={field.nullable === 'NO'}
                  {...controllerField}
                  label={camelCaseToWords(field.property)}
                  placeholder={`${t('manage_products.create_product.enter.additional')} ${camelCaseToWordsStartingWillLowerCase(field.property)}`}
                  type={inputType}
                  error={!!fieldState.error}
                  helperText={fieldState.error?.message ? t(fieldState.error.message) : ''}
                  fullWidth
                  margin='normal'
                  inputProps={inputType === 'number' ? { inputMode: 'numeric', pattern: '[0-9]*' } : {}}
                  onChange={handleChange}
                  sx={{...fieldStyle}}
                />
              );
            }}
          />
      ))}
      </Stack>
    </form>
  )
}