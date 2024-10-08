import React, { CSSProperties, useEffect, useState } from 'react';
import { Autocomplete, Box, Button, Card, CardContent, CardHeader, Grid, IconButton, ImageList, ImageListItem, Pagination, Rating, Stack, TablePagination, TextField, Tooltip, Typography } from '@mui/material';
import RefreshIcon from '@mui/icons-material/Refresh';
import StarIcon from '@mui/icons-material/Star';
import StarHalfIcon from '@mui/icons-material/StarHalf';
import StarBorderIcon from '@mui/icons-material/StarBorder';
import AddIcon from '@mui/icons-material/Add';
import axios from 'axios';
import { environment } from '../utils/constants';
import handleAxiosException from '../services/apiService';
import { BasicProduct, Role } from '../utils/types';
import productNotFound from '/src/assets/no-product-picture.png'
import { useTranslation } from 'react-i18next';
import { Controller, useForm } from 'react-hook-form';
import { getJwtToken } from '../services/tokenService';
import { z } from 'zod';
import { zodResolver } from '@hookform/resolvers/zod';
import { addToCart, getTotalAmountOfProducts } from '../services/cartService';
import { isUserSignIn } from '../services/sessionService';
import { toast } from 'sonner';
import VisibilityIcon from '@mui/icons-material/Visibility';
import ViewProductDetailsDialog from '../components/reusable/ViewProductDetailsDialog';
import { updatePreference } from '../services/preferencesService';

type DefaultProductData = {
  content: BasicProduct[]
  totalElements: number
}

const schema = z.object({
  category: z.string().min(1).optional()
});

type Category = z.infer<typeof schema>;

type ProductPageProps = {
  setLoading: (state: boolean) => void
  style: CSSProperties
  setNumberOfProductsInCart: (value: number) => void
  activeRole: Role
}

const ProductsPage = ({ setLoading, style, setNumberOfProductsInCart, activeRole } : ProductPageProps) => {
  const rowsPerPageOptions = [ 5, 10, 15, 20 ]
  const [ products, setProducts ] = useState<BasicProduct[]>([]);
  const [ categories, setCategories ] = useState<string[]>([]);
  const [ pickedCategory, setPickedCategory ] = useState<string | undefined>(undefined)
  const [ totalElements, setTotalElements ] = useState<number>(0)
  const [ pageSize, setPageSize ] = useState<number>(10);
  const [ currentPage, setCurrentPage ] = useState<number>(0);
  const { t } = useTranslation();
  const [ visibleViewProductDetailsDialog, setVisibleViewProductDetailsDialog ] = useState<number | undefined>(undefined)

  const { control, watch, formState, handleSubmit } = useForm<Category>({
    resolver: zodResolver(schema)
  });
  const { errors, isValid } = formState;

  useEffect(() => {
    fetchProducts(currentPage, pageSize);
    fetchCategories()
  }, []);

  const fetchProducts = async (pageNr: number, pageSize: number, category: string | undefined = undefined) => {
    try {
      setLoading(true);
      const { data } = category ?
        await getProductsByCategory(pageNr, pageSize, category) :
        await getProducts(pageNr, pageSize);
      const products: BasicProduct[] = data.content;
      setProducts(products)
      setTotalElements(data.totalElements)

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  };
  
  const fetchCategories = async () => {
    try {
      setLoading(true);
      const { data } = await getCategories();
      setCategories(data);

    } catch (e) {
      handleAxiosException(e)

    } finally {
      setLoading(false)
    }
  }

  const getProducts = async (pageNr: number, pageSize: number) => {
    return axios.get<DefaultProductData>(`${environment.apiBaseUrl}/products?page=${pageNr}&size=${pageSize}`);
  }

  const getCategories = async () => {
    return axios.get<string[]>(`${environment.apiBaseUrl}/categories`, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    });
  }

  const getProductsByCategory = async (pageNr: number, pageSize: number, categoryName: string) => {
    return axios.get<DefaultProductData>(`${environment.apiBaseUrl}/products/category/${categoryName}`, {
      headers: {
        Authorization: `Bearer ${getJwtToken()}`
      }
    })
  }

  const onPageFromZeroChange = (event: any, pageNumber: number) => {
    setCurrentPage(pageNumber);
    fetchProducts(pageNumber, pageSize, pickedCategory)
  };

  const onPageFromOneChange = (event: any, pageNumber: number) => {
    setCurrentPage(pageNumber - 1);
    fetchProducts(pageNumber - 1, pageSize, pickedCategory)
  };

  const onResetClicked = () => {
    fetchProducts(currentPage, pageSize, pickedCategory);
  };

  const changeRowsPerPage = (event: any): void => {
    const newPageSize = event.target.value
    setPageSize(newPageSize)
    fetchProducts(currentPage, newPageSize, pickedCategory)
  }

  const onValid = (data: Category) => {
    const category: string | undefined = data.category;
    if (category !== undefined) {
      updatePreference("category", category, 1.5)
    }
    setPickedCategory(category)
    fetchProducts(currentPage, pageSize, category)
  }

  const getStarArray = (averageRating: number) => {
    const fullStars = Math.floor(averageRating);
    const halfStars = Math.ceil(averageRating - fullStars);
    const emptyStars = 5 - fullStars - halfStars;
    return [
      ...Array(fullStars).fill(null).map((_, index) => (
        <StarIcon key={`full-${index}`} />
      )),
      ...Array(halfStars).fill(null).map((_, index) => (
        <StarHalfIcon key={`half-${index}`} />
      )),
      ...Array(emptyStars).fill(null).map((_, index) => (
        <StarBorderIcon key={`empty-${index}`} />
      ))
    ];
  };

  return (
    <Box sx={{ ...style }}>
      <Stack spacing={2}>
        <Stack direction='row' spacing={4} justifyContent='center'>
          <IconButton onClick={onResetClicked}>
            <RefreshIcon />
          </IconButton>

          <Box sx={{ width: '40%' }}>
            <form onSubmit={handleSubmit(onValid)} noValidate>
              <Stack direction='row' spacing={3}>
                <Controller
                  name='category'
                  control={control}
                  render={({ field }) => (
                    <Autocomplete
                      options={categories}
                      onChange={(e, value) => field.onChange(value ? value : undefined)}
                      isOptionEqualToValue={(option: any, value: any) => option.value === value?.value}
                      value={field.value || ''}
                      fullWidth
                      renderInput={(params) => (
                        <TextField
                          {...params}
                          label={t('manage_products.view_product.label.category')}
                          error={Boolean(errors.category)}
                          helperText={errors.category?.message && t(errors.category.message)}
                          placeholder={t('manage_products.view_product.enter.category')}
                        />
                      )}
                    />
                  )}
                />

                <Button type='submit' variant='contained' disabled={!isValid || pickedCategory === watch('category') }>
                  { watch('category') === undefined && pickedCategory !== undefined ? t('manage_products.view_product.clear') : t('manage_products.view_product.find') }
                </Button>
              </Stack>
            </form>
          </Box>
        </Stack>

        <Box display='flex' justifyContent='space-between' alignItems='center'>
          <Box>
            <Typography variant='h6'>{ pickedCategory ? t(pickedCategory) : t('manage_products.view_product.all') }:</Typography>
          </Box>
          <Box flexGrow={1} display='flex' justifyContent='center'>
            <Pagination
              count={Math.ceil(totalElements / pageSize)}
              page={currentPage + 1}
              onChange={onPageFromOneChange}
            />
          </Box>
        </Box>
      </Stack>



      <ImageList cols={5} gap={15} sx={{ marginTop: '20px' }}>
        {products.map((product, index) => (
          <ImageListItem key={index}>
            <Card elevation={3} sx={{
              width: 300,
              height: 290,
              display: 'flex',
              flexDirection: 'column',
              margin: '2px'
            }}>
              <CardHeader
                title={
                  <Box
                    marginTop='10px'
                    marginRight='10px'
                    marginLeft='10px'
                    component="img"
                    sx={{
                      height: 90,
                      width: '35%',
                      maxHeight: { xs: 90, md: 90 },
                      maxWidth: { xs: '100%', md: '100%' },
                    }}
                    alt={t('manage_products.view_product.label.product_image')}
                    src={product?.imageUrl ?? productNotFound}
                  />
                }
              />
              <CardContent>
                <Box className="card-title">
                  <Typography sx={{
                    whiteSpace: 'nowrap',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    maxWidth: '100%'
                  }} fontWeight='fontWeightBold' variant='subtitle1'>{product.name}</Typography>
                </Box>

                <Grid container>
                  <Grid container item xs={6} marginTop='20px' rowSpacing={1}>
                    <Grid item xs={8}>{t('manage_products.column.price')}: {product.price}</Grid>
                    <Grid item xs={6}>{t('manage_products.column.rate')}:</Grid>

                    {product.archival || product.quantity === 0 ? (
                      <Grid item xs={8} color='red'>{t('manage_products.view_product.unavailable')}</Grid>
                    ) : (
                      <Grid item xs={8} color='green'>{t('manage_products.view_product.available')}</Grid>
                    )}
                  </Grid>

                  <Grid container item xs={6}>
                    <Grid item xs={5} />
                    <Grid item xs={3}>
                      <Tooltip title={t('manage_prodcuts.view_product.show_details')} placement='right' children={
                        <IconButton onClick={() => {
                          setVisibleViewProductDetailsDialog(product.id)
                        }}>
                          <VisibilityIcon />
                        </IconButton>
                      } />
                    </Grid>

                    {!product.archival && product.quantity > 0 && isUserSignIn() && activeRole === Role.CLIENT ? (
                      <Grid item xs={3}>
                        <Tooltip title={t('manage_products.view_product.add_to_cart')} placement='right' children={
                          <IconButton onClick={() => {
                            addToCart(product)
                            setNumberOfProductsInCart(getTotalAmountOfProducts())
                            toast.success(t('manage_products.view_product.add_to_cart.success'))
                          }}>
                            <AddIcon />
                          </IconButton>
                        } />
                      </Grid>
                    ) : <Grid item xs={3} />}
                    <Grid item xs={1} />

                    {getStarArray(product.averageRating)}
                  </Grid>
                </Grid>

              </CardContent>
            </Card>
          </ImageListItem>
        ))}
      </ImageList>

      {
        visibleViewProductDetailsDialog &&
        <ViewProductDetailsDialog
          productId={visibleViewProductDetailsDialog}
          open={Boolean(setVisibleViewProductDetailsDialog)}
          onClose={() => setVisibleViewProductDetailsDialog(undefined)}
          activeRole={activeRole}
          setNumberOfProductsInCart={setNumberOfProductsInCart}
          setVisibleViewProductDetailsDialog={setVisibleViewProductDetailsDialog}
          setLoading={setLoading}
        />
      }

      <TablePagination
        component='div'
        page={currentPage}
        onPageChange={onPageFromZeroChange}
        count={totalElements}
        rowsPerPage={pageSize}
        rowsPerPageOptions={rowsPerPageOptions}
        onRowsPerPageChange={changeRowsPerPage}
      />
    </Box>
  );
};

export default ProductsPage;