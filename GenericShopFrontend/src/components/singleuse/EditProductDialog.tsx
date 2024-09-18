import { Button, Dialog, DialogActions, DialogContent, DialogTitle, Grid, Stack, TextField } from "@mui/material";
import { useForm } from "react-hook-form";
import z from "zod"
import { environment } from "../../utils/constants";
import { CSSProperties, useEffect, useState } from "react";
import { zodResolver } from "@hookform/resolvers/zod";
import { useTranslation } from "react-i18next";
import axios from "axios";
import { getJwtToken } from "../../services/tokenService";
import handleAxiosException from "../../services/apiService";
import { BasicProduct, BasicProductWithVersion } from "../../utils/types";
import { toast } from "sonner";

const schema = z.object({
  price: z.number({ message: 'manage_products.create_product.error.price.positive' }).positive('manage_products.create_product.error.price.positive'),
  quantity: z.number({ message: 'manage_products.create_product.error.price.positive'}).positive('manage_products.create_product.error.quantity.positive'),
  imageUrl: z.string().nullable()
})

type EditProduct = z.infer<typeof schema>

type EditProductDialogProps = {
  productId: number
  updateList: (productId: number, data: BasicProduct) => void
  open: boolean
  onClose: () => void
  style?: CSSProperties
  setLoading: (loading: boolean) => void
}

export default function EditProductDialog({ productId, updateList, open, onClose, setLoading, style } : EditProductDialogProps) {
  const { t } = useTranslation();
  const [ product, setProduct ] = useState<BasicProductWithVersion | undefined>(undefined)
  const [ isValid, setIsValid ] = useState<boolean>(false)

  useEffect(() => {
    sendGetProductRequest(productId)
  }, [productId])

  const getProduct = async (producId: number) => {
    return axios.get(`${environment.apiBaseUrl}/products/id/${producId}/short`);
  }

  const sendGetProductRequest = async (productId: number) => {
    try {
      setLoading(true)
      const { data } = await getProduct(productId);
      setProduct(data);

    } catch (e) {
      handleAxiosException(e)
      onClose()

    } finally {
      setLoading(false)
    }
  }

  return (
    <Dialog open={open} onClose={onClose} sx={{ ...style, marginTop: '4vh' }} maxWidth='sm'>
      <DialogTitle align='center' fontSize='35px'>Edit product</DialogTitle>
      <DialogContent>
        {
          product && (
            <EditProductForm onClose={onClose} product={product} productId={productId} updateList={updateList} setLoading={setLoading} setIsValid={setIsValid} />
          )
        }
      </DialogContent>
      <DialogActions>
        <Stack direction='row' spacing={3}>
          <Button onClick={() => onClose()}>{t('manage_products.button.back')}</Button>
          <Button type='submit' variant='contained' form='create_product.edit_product' disabled={!isValid}>
            {t('manage_products.button.submit')}
          </Button>
        </Stack>
      </DialogActions>
    </Dialog>
  )
}

type EditProductFormProps = {
  productId: number
  product: BasicProductWithVersion
  updateList: (productId: number, data: BasicProduct) => void
  onClose: () => void
  setLoading: (loading: boolean) => void
  setIsValid: (state: boolean) => void
  style?: CSSProperties
}

function EditProductForm({ productId, product, updateList, onClose, setLoading, setIsValid, style } : EditProductFormProps) {
  const { t } = useTranslation()
  const { register, handleSubmit, formState, reset } = useForm<EditProduct>({
    mode: 'onChange',
    resolver: zodResolver(schema),
    defaultValues: {
      price: 0,
      quantity: 0,
      imageUrl: null  
    }
  })
  const { errors, isValid } = formState
  const fieldStyle = { height: '64px', width: '100%' }

  useEffect(() => {
    reset(product)
  }, [open])

  useEffect(() => {
    setIsValid(isValid)
  }, [isValid])

  const onValid = async (formData: EditProduct) => {
    try {
      setLoading(true)
      const version = product.version;
      const updateRequestData = { ...formData, version }
      const { data } = await editProduct(productId, updateRequestData)
      updateList(productId, data)
      toast.success(t('manage_products.edit_product.success'))
      reset(data)
      onClose();

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const editProduct = async (productId: number, data: EditProduct) => {
    return axios.put<BasicProductWithVersion>(`${environment.apiBaseUrl}/products/id/${productId}`, data, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  return (
    <form id="create_product.edit_product" onSubmit={handleSubmit(onValid)} noValidate>
      <Grid container columnSpacing={4} rowSpacing={6} justifyContent='center' marginTop='5px' marginBottom='30px'>
        <Grid item xs={12} sm={12}>
          <TextField label={t('manage_products.edit_product.label.price')} {...register('price', { valueAsNumber: true })}
            placeholder={t('manage_products.edit_product.enter.price')}
            error={Boolean(errors.price?.message)}
            helperText={errors.price?.message && t(errors.price.message)}
            sx={fieldStyle}
            autoComplete='true'
          />
        </Grid>

        <Grid item xs={12} sm={12}>
          <TextField label={t('manage_products.edit_product.label.quantity')} {...register('quantity', { valueAsNumber: true })}
            placeholder={t('manage_products.edit_product.enter.quantity')}
            error={Boolean(errors.quantity?.message)}
            helperText={errors.quantity?.message && t(errors.quantity.message)}
            sx={fieldStyle}
            autoComplete='true'
          />
        </Grid>

        <Grid item xs={12} sm={12}>
          <TextField label={t('manage_products.edit_product.label.product_image')} {...register('imageUrl')}
            placeholder={t('manage_products.edit_product.enter.image_url')}
            error={Boolean(errors.imageUrl?.message)}
            helperText={errors.imageUrl?.message && t(errors.imageUrl.message)}
            sx={fieldStyle}
            autoComplete='true'
          />
        </Grid>
      </Grid>
    </form>
  )
}