import axios from 'axios';
import { NextApiRequest, NextApiResponse } from 'next';

export default async function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method === 'GET') {
    try {
      // Extract query parameters from the request
      const { input, 'gplaces[components]': gplacesComponents } = req.query;

      // Construct the API URL with query parameters
      const apiUrl = `https://api-uat.whitbread.co.uk/v1/autocomplete?input=${encodeURIComponent(
        input as string
      )}&gplaces[components]=${encodeURIComponent(gplacesComponents as string)}`;

      // Make the API call
      const response = await axios.get(apiUrl);

      // Return the API response to the client
      res.status(200).json(response.data);
    } catch (error) {
      // Narrow the error type
      const errorMessage = error instanceof Error ? error.message : 'Unknown error';

      // Log the error for debugging
      console.error('Error during API call:', errorMessage);

      // Return an error response
      res.status(500).json({ error: 'Internal Server Error', details: errorMessage });
    }
  } else {
    // Return a 405 Method Not Allowed if the request method is not GET
    res.setHeader('Allow', ['GET']);
    res.status(405).end(`Method ${req.method} Not Allowed`);
  }
}
