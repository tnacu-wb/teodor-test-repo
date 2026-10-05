export default function liveness(req, res) {
  res.status(200).json({ status: 'ok' });
}
