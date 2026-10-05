export default function readiness(req, res) {
  res.status(200).json({ status: 'ok' });
}
