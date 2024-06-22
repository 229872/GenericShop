import { Autocomplete, Button, Chip, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Stack, TextField, Typography } from "@mui/material";
import axios from "axios";
import React, { CSSProperties, useEffect } from "react";
import { Controller, useFieldArray, useForm } from "react-hook-form";
import z from "zod";
import { environment, regex } from "../../utils/constants";
import { useTranslation } from "react-i18next";
import AddIcon from '@mui/icons-material/Add';
import { zodResolver } from "@hookform/resolvers/zod";
import { getJwtToken } from "../../services/tokenService";
import handleAxiosException from "../../services/apiService";
import { toast } from "sonner";

const types = ['NUMBER', 'BIG_NUMBER', 'TEXT', 'FRACTIONAL_NUMBER', 'LOGICAL_VALUE'] as const;
const constraints = ['UNIQUE', 'REQUIRED'] as const;

const schema = z.object({
  name: z.string({ message: 'manage_products.create_category.error.category_name.required' })
    .regex(regex.TABLE_NAME, 'manage_products.create_category.error.category_name.required'),
  properties: z.array(
    z.object({
      propertyName: z.string({ message: 'manage_products.create_category.error.propertyName.required' }).min(1, 'manage_products.create_category.error.propertyName.required'),
      propertyType: z.enum(types, { message: 'manage_products.create_category.error.propertyType.not_supported' }),
      propertyConstraints: z.array(z.enum(constraints, { message: 'manage_products.create_category.error.propertyConstraint.not_supported' }))
    })
  ).optional()
});

type NewCategory = z.infer<typeof schema>;

type CategorySchema = {
  categoryName: string
  properties: { [key: string]: string[] };
}


type CreateCategoryDialogProps = {
  open: boolean
  onClose: () => void
  setLoading: (loading: boolean) => void
  style?: CSSProperties
}

export default function CreateCategoryDialog({ open, onClose, setLoading, style } : CreateCategoryDialogProps) {
  const { t } = useTranslation();
  const fieldStyle = { height: '80px' }
  const { register, reset, formState, handleSubmit, control } = useForm<NewCategory>({
    resolver: zodResolver(schema),
    defaultValues: { name: '', properties: [] },
    mode: 'onChange'
  })
  const { errors, isValid } = formState;
  const { fields, append } = useFieldArray({ control, name: 'properties' });

  useEffect(() => {
    reset()
  }, [open])

  const translate = (message: string | undefined) : string => {
    return message ?? '';
  }

  const onValid = async (data: NewCategory) => {
    const propertiesMap: Map<string, string[]> = new Map();

    if (data.properties) {
      data.properties.forEach(property => {
        const propertyDetails: string[] = [property.propertyType, ...property.propertyConstraints];
        propertiesMap.set(property.propertyName, propertyDetails);
      })
    }

    const requestData: CategorySchema = {
      categoryName: data.name,
      properties: Object.fromEntries(propertiesMap)
    }

    try {
      setLoading(true)
      await createCategory(requestData)
      toast.success(t('manage_products.create_category.success'))
      onClose()

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }

  }

  const createCategory = async (data: CategorySchema) => {
    return axios.post(`${environment.apiBaseUrl}/categories`, data, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }


  return (
    <Dialog open={open} onClose={onClose} sx={{ ...style, marginTop: '7vh', maxHeight: '85vh' }} fullWidth maxWidth='md'>
      <DialogTitle variant='h3' textAlign='center'>{t('manage_products.create_category.title')}</DialogTitle>
      <DialogContent>
        <form id='manage_products.create_category' onSubmit={handleSubmit(onValid)} noValidate>
          <Grid container justifyContent='center' spacing={5} sx={{ marginTop: '1px' }}>
            <Grid item xs={12} sm={3}></Grid>
            <Grid item xs={12} sm={6}>
              <TextField
                {...register('name')}
                label={t('manage_products.create_category.label.name')}
                placeholder={t('manage_products.create_category.enter.name')}
                error={Boolean(errors.name)}
                fullWidth
                helperText={errors.name?.message && t(errors.name.message)}
                sx={{ ...fieldStyle }}
              />
            </Grid>
            <Grid item xs={12} sm={3}></Grid>

            {fields.map((field, index) => (
             <React.Fragment key={index}>
                <Grid item xs={12} sm={4}>
                  <TextField
                    label={t('manage_products.create_category.label.propertyName')}
                    placeholder={t('manage_products.create_category.enter.propertyName')}
                    {...register(`properties.${index}.propertyName` as const)}
                    error={Boolean(errors.properties?.[index]?.propertyName)}
                    helperText={t(translate(errors.properties?.[index]?.propertyName?.message))}
                    sx={fieldStyle}
                  />
                </Grid>

                <Grid item xs={12} sm={3}>
                  <Controller
                    name={`properties.${index}.propertyType` as const}
                    control={control}
                    render={({ field }) => (
                      <Autocomplete
                        options={types}
                        sx={fieldStyle}
                        onChange={(e, value) => field.onChange(value)}
                        isOptionEqualToValue={(option: any, value: any) => option.value === value.value}
                        value={types.find(option => option === field.value) || ''}
                        renderInput={(params) => (
                          <TextField
                            {...params}
                            label={t('manage_products.create_category.label.propertyType')}
                            error={Boolean(errors.properties?.[index]?.propertyType)}
                            helperText={t(translate(errors.properties?.[index]?.propertyType?.message))}
                            placeholder={t('manage_products.create_category.enter.propertyType')}
                          />
                        )}
                      />
                    )}
                  />
                </Grid>

                <Grid item xs={12} sm={5}>
                  <Controller
                    name={`properties.${index}.propertyConstraints` as const}
                    control={control}
                    render={({ field }) => (
                      <Autocomplete
                        multiple
                        options={constraints}
                        sx={fieldStyle}
                        onChange={(e, value) => field.onChange(value)}
                        value={field.value || []}
                        renderTags={(value: ('UNIQUE' | 'REQUIRED')[], getTagProps) =>
                          value.map((option: string, index: number) => {
                            const { key, ...tagProps } = getTagProps({ index });
                            return (
                              <Chip variant="outlined" label={option} key={key} {...tagProps} />
                            );
                          })
                        }
                        renderInput={(params) => (
                          <TextField
                            {...params}
                            label={t('manage_products.create_category.label.propertyConstraints')}
                            error={Boolean(errors.properties?.[index]?.propertyConstraints)}
                            helperText={t(translate(errors.properties?.[index]?.propertyConstraints?.message))}
                            placeholder={ !field.value ? t('manage_products.create_category.enter.propertyConstraints') : ''}
                          />
                        )}
                      />
                    )}
                  />
                </Grid>
              </React.Fragment>
            ))}
          </Grid>
          <Stack spacing={3} marginTop='30px'>
            <Typography align='center' variant='h6'>{t('manage_products.create_category.subtitle')}</Typography>

            <Button onClick={() => append({ propertyName: '', propertyType: 'TEXT', propertyConstraints: [] })}>
              <AddIcon />
              <Typography marginLeft='10px'>{t('manage_products.create_category.button.label.add')}</Typography>
            </Button>
          </Stack>


        </form>
      </DialogContent>
      <DialogActions>
        <Stack direction='row' spacing={3}>
          <Button onClick={() => onClose()}>{t('manage_products.button.back')}</Button>
          <Button type='submit' disabled={!isValid} form='manage_products.create_category'>{t('manage_products.button.submit')}</Button>
        </Stack>
      </DialogActions>
    </Dialog>
  )
}