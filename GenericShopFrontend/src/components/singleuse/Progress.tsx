import { Box, CircularProgress, LinearProgress } from "@mui/material"

type ProgressParams = {
  loading: boolean
}

export default function Progress({loading}: ProgressParams) {
  const overlayStyle = {
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    backgroundColor: 'rgba(0, 0, 0, 0.15)', 
    zIndex: 20000
};

  return (
    <>
      {loading && (
        <Box sx={overlayStyle}></Box>
      )}

      {loading ? 
        <LinearProgress color='warning' style={{ position: 'sticky', top: '65px' }} /> 
        : <div style={{ height: '4px', position: 'sticky', top: '65px' }} />} 

      {loading && <CircularProgress color='inherit' variant='indeterminate' size={80} sx={{
            position: 'absolute',
            top: 'calc(50% - 40px)',
            left: 'calc(50% - 40px)',
            zIndex: 20000
        }} />}
    </>
  )
}